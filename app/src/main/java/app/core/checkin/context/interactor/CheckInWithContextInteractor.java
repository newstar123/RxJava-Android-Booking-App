package app.core.checkin.context.interactor;

import android.content.Context;

import java.util.List;

import app.CustomApplication;
import app.core.BaseInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkin.user.post.entity.EmailVerificationException;
import app.core.login.check.CheckAccountInteractor;
import app.core.payment.get.entity.GetPaymentCardModel;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.entity.NoPaymentException;
import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.mvp.bars.detail.checkin.open.model.OpenTabRequest;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.enums.OpenTabMethods;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.checkin.user.context.PostCheckInRestContextGateway;
import app.gateway.payment.get.context.GetPaymentsRestContextGateway;
import app.gateway.profile.get.context.GetContextProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CheckInWithContextInteractor implements BaseInteractor<OpenTabRequest, Observable<CheckInResponse>> {
    private final GetPaymentsRestContextGateway getPaymentsGateway;
    private final PostCheckInRestContextGateway postCheckInGateway;
    private final GetProfileInteractor getProfileInteractor;

    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public CheckInWithContextInteractor(Context context) {
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
        postCheckInGateway = new PostCheckInRestContextGateway(context);
        getPaymentsGateway = new GetPaymentsRestContextGateway(context);
        getProfileInteractor = new GetProfileInteractor(context, new GetContextProfileRestGateway(context));
    }

    @Override public Observable<CheckInResponse> process(OpenTabRequest request) {
        return androidAuthTokenGateway.get()
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .observeOn(Schedulers.io())
                .concatMap(token -> checkEmailVerified())
                .concatMap(token -> checkPaymentCards())
                .concatMap(successCheck -> checkIn(request))
                .doOnNext(checkInResponse ->
                        CustomApplication.get().getCheckInController().onCheckIn(checkInResponse.getCheckin()))
                .concatMap(this::saveIds)
                .concatMap(this::sendAnalytics);
    }

    private Observable<ProfileModel> checkEmailVerified() {
        return getProfileInteractor.process()
                .doOnNext(profileModel -> {/*if(profileModel.getIsEmailVerified() == 0)*/ throw new EmailVerificationException();});
    }

    private Observable<EmptyResponse> checkPaymentCards() {
        return getPaymentsGateway.get().concatMap(this::checkHasPayment);
    }

    private Observable<EmptyResponse> checkHasPayment(GetPaymentCardsModel getPaymentCardsModel) {
        List<GetPaymentCardModel> cards = getPaymentCardsModel.getCards();
        if (cards == null || cards.size() < 1)
            throw new NoPaymentException();
        return Observable.just(new EmptyResponse());
    }

    private Observable<CheckInResponse> checkIn(OpenTabRequest request) {
        return postCheckInGateway.post(request.getCheckinRequest());
    }

    private Observable<CheckInResponse> saveIds(CheckInResponse checkInResponse) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getId()))
                .concatMap(aLong -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getVendorId())))
                .concatMap(aLong -> Observable.just(checkInResponse));
    }

    private Observable<CheckInResponse> sendAnalytics(CheckInResponse response) {
        return MixpanelSendGateway.send(MixpanelEvents.getOpenTabEvent(OpenTabMethods.OPEN_TAB_BY_BEACON, response))
                .map(isOk -> response);
    }
}
