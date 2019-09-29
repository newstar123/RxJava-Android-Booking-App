package app.core.login.check.foreground;


import app.core.BaseOutputInteractor;
import app.core.init.token.entity.Token;
import app.core.login.check.entity.GuestUserException;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import rx.Observable;

public class ForegroundCheckAccountInteractor implements BaseOutputInteractor<Observable<Token>> {
    private final AndroidAuthTokenGateway authTokenGateway;

    public ForegroundCheckAccountInteractor(BaseActivity activity) {
        authTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<Token> process() {
        return  authTokenGateway.get()
                .doOnNext(ForegroundCheckAccountInteractor::checkLoggedIn);
    }

    public static void checkLoggedIn(Token token) {
        if (token.getAuthToken().equals(LoginResponse.GUEST_TOKEN))
            throw new GuestUserException();
    }
}
