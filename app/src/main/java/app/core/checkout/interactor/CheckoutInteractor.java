package app.core.checkout.interactor;

import android.content.Context;

import app.CustomApplication;
import app.core.BaseInteractor;
import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.CheckOutRequestBody;
import app.core.checkout.entity.PhoneVerificationException;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.enums.CloseTabMethods;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.checkout.PutCheckoutRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.profile.get.context.GetContextProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

public class CheckoutInteractor implements BaseInteractor<CheckOutRequestBody,Observable<CheckInResponse>> {
    private PutCheckoutRestGateway checkoutRestGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetCheckInInteractor getCheckInByIdInteractor;
    private final GetProfileInteractor getProfileInteractor;


    public CheckoutInteractor(Context context) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(context);
        checkoutRestGateway = new PutCheckoutRestGateway(context);
        getCheckInByIdInteractor = new GetCheckInByIdInteractor(context);
        getProfileInteractor = new GetProfileInteractor(context, new GetContextProfileRestGateway(context));
    }

    @Override public Observable<CheckInResponse> process(CheckOutRequestBody requestBody) {
        return networkPermissionGateway.check()
                .map(isOk -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(cacheCheckInId -> (long) cacheCheckInId)
                .filter(cacheCheckInId -> cacheCheckInId != 0)
                .concatMap(cacheCheckInId -> checkPhoneVerified())
                .concatMap(isOk -> checkoutRestGateway.put(requestBody.getCheckInId()))
                .doOnNext(emptyResponse -> LogToFileHandler.addLog("GetCheckInByIdRestGateway - call from-CheckoutInteractor"))
                .concatMap(isOk -> getCheckInByIdInteractor.process(requestBody.getCheckInId()))
                .doOnNext(checkInResponse -> CustomApplication.get().getCheckInController().onCheckOut(checkInResponse.getCheckin()))
                .concatMap(response -> sendAnalytics(response, requestBody.isClosedByUberRequest()))
                .concatMap(this::clearCache);
    }

    private Observable<ProfileModel> checkPhoneVerified() {
        return getProfileInteractor.process()
                .doOnNext(profileModel -> { if (profileModel.getIsPhoneVerified() == 0) throw new PhoneVerificationException();});
    }

    private Observable<CheckInResponse> sendAnalytics(CheckInResponse response, boolean closedByUberRequest) {
        return MixpanelSendGateway.send(MixpanelEvents.getCloseTabEvent(closedByUberRequest ?
                CloseTabMethods.CLOSE_TAB_BY_UBER_CALL : CloseTabMethods.CLOSE_TAB, response))
                .map(isOk -> response);
    }

    private Observable<CheckInResponse> clearCache(CheckInResponse model) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                .map(emptyId -> QorumSharedCache.checkTimeLeftToRide().save(BaseCacheType.INT, 0))
                .concatMap(emptyId -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0)))
                .map(emptyId -> QorumSharedCache.checkUberRideId().save(BaseCacheType.STRING, ""))
                .concatMap(emptyId -> Observable.just(QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG,model.getCheckin().getId())))
                .map(emptyId -> QorumSharedCache.checkFreeRideWarning().save(BaseCacheType.BOOLEAN,false))
                .doOnNext(val -> QorumSharedCache.checkOpenedBarName().save(BaseCacheType.STRING, ""))
                .concatMap(emptyId -> Observable.just(model))
                .doOnNext(checkInResponse -> QorumSharedCache.checkFreeRideDialogAlreadyShown().save(BaseCacheType.BOOLEAN, false));
    }
}
