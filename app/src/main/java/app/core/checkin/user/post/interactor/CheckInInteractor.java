package app.core.checkin.user.post.interactor;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.CustomApplication;
import app.core.BaseInteractor;
import app.core.beacon.bluetooth.entity.BluetoothNotAvailableException;
import app.core.beacon.bluetooth.entity.BluetoothStateException;
import app.core.beacon.bluetooth.interactor.CheckBluetoothInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkin.user.post.entity.CheckPhotoException;
import app.core.checkin.user.post.entity.EmailVerificationException;
import app.core.checkin.user.post.gateway.PostCheckInGateway;
import app.core.checkin.user.post.interactor.exception.CheckInAlreadyInUseException;
import app.core.login.check.CheckAccountInteractor;
import app.core.login.check.foreground.ForegroundCheckAccountInteractor;
import app.core.payment.get.entity.GetPaymentCardModel;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.entity.NoPaymentException;
import app.core.payment.get.gateway.GetPaymentsGateway;
import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdateOpenTabViewEvent;
import app.delivering.mvp.bars.detail.checkin.open.model.OpenTabRequest;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.enums.OpenTabMethods;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.checkin.user.PostCheckInRestGateway;
import app.gateway.payment.get.GetPaymentsRestGateway;
import app.gateway.permissions.bluetooth.CheckBluetoothPermissionGateway;
import app.gateway.profile.get.GetProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CheckInInteractor implements BaseInteractor<OpenTabRequest, Observable<CheckInResponse>> {
    private final ForegroundCheckAccountInteractor checkAccountInteractor;
    private final GetPaymentsGateway getPaymentsGateway;
    private final PostCheckInGateway postCheckInGateway;
    private final CheckBluetoothPermissionGateway bluetoothPermissionGateway;
    private final CheckBluetoothInteractor bluetoothInteractor;
    private final GetProfileInteractor getProfileInteractor;
    private boolean defaultPhotoValueFB;

    public CheckInInteractor(BaseActivity activity) {
        postCheckInGateway = new PostCheckInRestGateway(activity);
        checkAccountInteractor = new ForegroundCheckAccountInteractor(activity);
        getPaymentsGateway = new GetPaymentsRestGateway(activity);
        bluetoothPermissionGateway = new CheckBluetoothPermissionGateway(activity);
        bluetoothInteractor = new CheckBluetoothInteractor();
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
    }

    @Override public Observable<CheckInResponse> process(OpenTabRequest request) {
        return Observable.zip(getSharedCheckIn(), Observable.just((long)QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG)),
                (checkinId, barId) -> {
                    if (checkinId > 0 || barId > 0) throw new CheckInAlreadyInUseException();
                    return new EmptyResponse(); })
                .concatMap(val -> checkAccountInteractor.process())
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .concatMap(token -> bluetoothPermissionGateway.check())
                .concatMap(isOk -> bluetoothInteractor.process())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(th -> ignoreExceptionForDevicesWithoutBluetooth(th, request.isIgnoreBluetoothState()))
                .concatMap(this::checkProfilePhotoValue)
                .concatMap(this::checkProfileEmailAndPhoto)
                .concatMap(token -> getPaymentsGateway.get())
                .doOnNext(this::checkHasPayment)
                .doOnNext(token -> EventBus.getDefault().post(new UpdateOpenTabViewEvent()))
                .concatMap(token -> checkIn(request))
                .doOnNext(checkInResponse ->
                        CustomApplication.get().getCheckInController().onCheckIn(checkInResponse.getCheckin()))
                .concatMap(this::sendAnalytics)
                .concatMap(this::saveIds);
    }

    private Observable<Long> getSharedCheckIn() {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(val -> (long) val);
    }

    private Observable<EmptyResponse> checkProfilePhotoValue(EmptyResponse emptyResponse) {
        return Observable.just((boolean)QorumSharedCache.checkProfilePhotoVal().get(BaseCacheType.BOOLEAN))
                .doOnNext(this::getFBDefaultPhotoValue)
                .map(v -> emptyResponse);
    }

    private void getFBDefaultPhotoValue(Boolean b) {
        Observable.just((boolean)QorumSharedCache.checkFBPhoto().get(BaseCacheType.BOOLEAN))
                .subscribe(this::checkFBDefaultPhotoValue);
        if (defaultPhotoValueFB && !b) throw new CheckPhotoException();
    }

    private void checkFBDefaultPhotoValue(Boolean value) {
        if (value) defaultPhotoValueFB = value;
    }

    private Observable<EmptyResponse> checkProfileEmailAndPhoto(EmptyResponse emptyResponse) {
        return getProfileInteractor.process()
                .doOnNext(profileModel -> { if (profileModel.getIsEmailVerified() == 0) throw new EmailVerificationException(); })
                .map(profileModel -> emptyResponse);
    }

    private Observable<EmptyResponse> ignoreExceptionForDevicesWithoutBluetooth(Throwable throwable, boolean ignoreBluetoothState) {
        if (throwable instanceof BluetoothNotAvailableException ||
                (throwable instanceof BluetoothStateException && ignoreBluetoothState))
            return Observable.just(new EmptyResponse());
        else
            return Observable.error(throwable);
    }

    private Observable<CheckInResponse> sendAnalytics(CheckInResponse response) {
        return MixpanelSendGateway.send(MixpanelEvents.getOpenTabEvent(OpenTabMethods.OPEN_TAB, response))
                .map(isOk -> response);
    }

    private Observable<CheckInResponse> saveIds(CheckInResponse checkInResponse) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getId()))
                .concatMap(aLong -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getVendorId())))
                .concatMap(aLong -> Observable.just(checkInResponse));
    }

    private Observable<CheckInResponse> checkIn(OpenTabRequest request) {
        return postCheckInGateway.post(request.getCheckinRequest());
    }

    private void checkHasPayment(GetPaymentCardsModel getPaymentCardsModel) {
        List<GetPaymentCardModel> cards = getPaymentCardsModel.getCards();
        if (cards == null || cards.size() < 1)
            throw new NoPaymentException();
    }
}
