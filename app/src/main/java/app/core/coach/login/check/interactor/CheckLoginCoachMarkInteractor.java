package app.core.coach.login.check.interactor;

import app.core.BaseOutputInteractor;
import app.core.coach.entity.BooleanResponse;
import app.core.init.token.entity.Token;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckLoginCoachMarkInteractor implements BaseOutputInteractor<Observable<BooleanResponse>> {
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public CheckLoginCoachMarkInteractor(BaseActivity activity) {
        androidAuthTokenGateway = new AuthTokenWithContextGateway(activity);
    }

    @Override public Observable<BooleanResponse> process() {
        return Observable.just((boolean)QorumSharedCache.checkLoginCoachMark().get(BaseCacheType.BOOLEAN))
                .filter(isOk -> !isOk)
                .concatMap(isOk -> androidAuthTokenGateway.get())
                .concatMap(this::checkToken);
    }

    private Observable<BooleanResponse> checkToken(Token token) {
        return Observable.create(subscriber -> {
            if (token.getAuthToken().equals(LoginResponse.GUEST_TOKEN)){
                subscriber.onNext(new BooleanResponse(true));
                subscriber.onCompleted();
            } else {
                subscriber.onError(new RuntimeException());
            }
        });
    }
}
