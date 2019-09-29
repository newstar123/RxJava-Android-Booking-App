package app.core.checkout.delay;

import android.content.Context;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.CustomApplication;
import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.PhoneVerificationException;
import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.enums.CloseTabMethods;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.checkout.delay.delete.DeleteDelayCheckoutRestGateway;
import app.gateway.checkout.delay.post.PostDelayCheckoutRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.profile.get.context.GetContextProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

public class DelayCheckoutInteractor implements DelayCheckoutInterface {
    public static final int DELAY = 120000;
    public static final long DELAY_IN_MINUTES = DELAY / 60 / 1000;
    private final PostDelayCheckoutRestGateway postDelayCheckoutRestGateway;
    private final DeleteDelayCheckoutRestGateway deleteDelayCheckoutRestGateway;
    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetCheckInInteractor getCheckInByIdInteractor;
    private final GetProfileInteractor getProfileInteractor;
    private final HashMap autoCheckoutNotificationParams;
    private boolean isCheckoutDeleted;

    public DelayCheckoutInteractor(Context context) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(context);
        getCheckInByIdInteractor = new GetCheckInByIdInteractor(context);
        getProfileInteractor = new GetProfileInteractor(context, new GetContextProfileRestGateway(context));
        postDelayCheckoutRestGateway = new PostDelayCheckoutRestGateway(context);
        deleteDelayCheckoutRestGateway = new DeleteDelayCheckoutRestGateway(context);
        autoCheckoutNotificationParams = new HashMap();
    }

    @Override
    public Observable<CheckInResponse> post() {
        return networkPermissionGateway.check()
                .concatMap(isOk -> checkPhoneVerified())
                .map(isOk -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(checkId -> (long) checkId)
                .filter(checkInId -> checkInId > 0)
                .doOnNext(aLong -> LogToFileHandler.addLog("GetCheckInByIdRestGateway - call from-DelayCheckoutByGPSInteractor"))
                .concatMap(getCheckInByIdInteractor::process)
                .concatMap(this::startDelayCheckOut)
                .doOnNext(emptyResponse -> LogToFileHandler.addLog("Auto-check out timer - start"))
                .delay(DELAY, TimeUnit.MILLISECONDS)
                .filter(checkIn -> !isCheckoutDeleted)
                .doOnNext(aLong -> LogToFileHandler.addLog("Auto-check out timer - end"))
                .concatMap(this::sendAnalytics)
                .concatMap(this::clearCache);
    }

    private Observable<ProfileModel> checkPhoneVerified() {
        return getProfileInteractor.process()
                .doOnNext(profileModel -> {
                    if (profileModel.getIsPhoneVerified() == 0)
                        throw new PhoneVerificationException();
                });
    }

    private Observable<CheckInResponse> startDelayCheckOut(CheckInResponse checkInResponse) {
        return postDelayCheckoutRestGateway.put(checkInResponse.getCheckin().getId())
                .doOnNext(emptyResponse -> {
                    fillUpAutoCheckInParams(checkInResponse);
                    QorumNotifier.notify(CustomApplication.get(), NotificationType.TAB_AUTO_CLOSE_TIMER, autoCheckoutNotificationParams);
                })
                .map(emptyResponse -> checkInResponse);
    }

    private Observable<CheckInResponse> sendAnalytics(CheckInResponse response) {
        return MixpanelSendGateway.send(MixpanelEvents.getCloseTabEvent(CloseTabMethods.CLOSE_TAB_BY_BEACON, response))
                .map(isOk -> response);
    }

    private Observable<CheckInResponse> clearCache(CheckInResponse model) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                .map(checkinId -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0)))
                .map(checkinBarId -> Observable.just(QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, model.getCheckin().getId())))
                .map(checkinBarId -> model)
                .doOnNext(aLong -> isCheckoutDeleted = false)
                .doOnNext(checkInResponse -> QorumSharedCache.checkFreeRideDialogAlreadyShown().save(BaseCacheType.BOOLEAN, false));
    }

    @Override
    public Observable<EmptyResponse> delete() {
        return networkPermissionGateway.check()
                .map(isOk -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(checkInId -> (long) checkInId)
                .filter(checkinId -> checkinId > 0)
                .concatMap(deleteDelayCheckoutRestGateway::put)
                .concatMap(emptyResponse -> checkAutoCheckOutParams())
                .doOnNext(emptyResponse -> {
                        QorumNotifier.notify(CustomApplication.get(),
                                NotificationType.TAB_AUTO_CLOSING_STOPPED,
                                autoCheckoutNotificationParams);
                })
                .doOnNext(emptyResponse -> {
                    isCheckoutDeleted = true;
                    autoCheckoutNotificationParams.clear();
                })
                .map(hashMap -> new EmptyResponse());
    }

    private Observable<HashMap> checkAutoCheckOutParams() {
        if (autoCheckoutNotificationParams.isEmpty() || autoCheckoutNotificationParams.size() < 3){
            return Observable.just((long)QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                    .concatMap(getCheckInByIdInteractor::process)
                    .doOnNext(this::fillUpAutoCheckInParams)
                    .map(checkInResponse -> autoCheckoutNotificationParams);
        } else
            return Observable.just(autoCheckoutNotificationParams);
    }

    private void fillUpAutoCheckInParams(CheckInResponse checkInResponse) {
        autoCheckoutNotificationParams.put(QorumNotifier.CHECK_IN_VENDOR_NAME, checkInResponse.getCheckin().getVendor().getName());
        autoCheckoutNotificationParams.put(QorumNotifier.CHECK_IN_ID, checkInResponse.getCheckin().getId());
        autoCheckoutNotificationParams.put(QorumNotifier.VENDOR_ID, checkInResponse.getCheckin().getVendorId());
    }
}
