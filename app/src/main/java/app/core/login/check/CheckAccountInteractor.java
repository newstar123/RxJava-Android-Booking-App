package app.core.login.check;

import app.core.BaseOutputInteractor;
import app.core.init.token.entity.Token;
import app.core.login.check.entity.GuestUserException;
import app.core.login.facebook.entity.LoginResponse;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAccountTokenInvalidator;
import app.gateway.auth.AndroidAuthTokenGateway;
import rx.Observable;

public class CheckAccountInteractor implements BaseOutputInteractor<Observable<Token>> {
    private final AndroidAuthTokenGateway authTokenGateway;
    private final PermissionInteractor permissionInteractor;

    public CheckAccountInteractor(BaseActivity activity) {
        authTokenGateway = new AndroidAuthTokenGateway(activity);
        permissionInteractor = new PermissionInteractor(activity);
    }

    @Override public Observable<Token> process() {
        return permissionInteractor.process()
                .concatMap(isGranted -> authTokenGateway.get())
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .onErrorResumeNext(this::handleGuestUser);
    }

    public static void checkLoggedIn(Token token) {
        if (token.getAuthToken().equals(LoginResponse.GUEST_TOKEN))
            throw new GuestUserException();
    }

    private Observable<Token> handleGuestUser(Throwable throwable) {
        if (throwable instanceof GuestUserException)
            return AndroidAccountTokenInvalidator.invalidateToken()
                    .concatMap(o -> authTokenGateway.get());
        return Observable.error(throwable);
    }
}
