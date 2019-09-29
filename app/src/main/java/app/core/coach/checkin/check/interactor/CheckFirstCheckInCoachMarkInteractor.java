package app.core.coach.checkin.check.interactor;

import app.core.BaseOutputInteractor;
import app.core.coach.entity.BooleanResponse;
import app.core.init.token.entity.Token;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckFirstCheckInCoachMarkInteractor implements BaseOutputInteractor<Observable<BooleanResponse>> {
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public CheckFirstCheckInCoachMarkInteractor(BaseActivity activity) {
        androidAuthTokenGateway = new AuthTokenWithContextGateway(activity);
    }

    @Override public Observable<BooleanResponse> process() {
        return Observable.zip(Observable.just(QorumSharedCache.checkCheckInCoachMark().get(BaseCacheType.BOOLEAN)), androidAuthTokenGateway.get(), this::checkToken)
                .map(BooleanResponse::new);
    }

    private Boolean checkToken(Boolean isShowed, Token token) {
        return !isShowed && !token.getAuthToken().equals(LoginResponse.GUEST_TOKEN);
    }
}
