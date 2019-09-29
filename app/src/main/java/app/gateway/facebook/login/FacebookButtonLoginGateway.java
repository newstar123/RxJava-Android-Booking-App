package app.gateway.facebook.login;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import app.core.init.token.entity.CancelFacebookLoginException;
import app.core.login.facebook.gateway.FacebookLoginGateway;
import app.delivering.component.BaseActivity;
import app.gateway.facebook.FacebookCallbackManagerInstance;
import rx.Observable;


public class FacebookButtonLoginGateway implements FacebookLoginGateway {
    private final LoginButton loginButton;

    public FacebookButtonLoginGateway(BaseActivity activity) {
        loginButton = new LoginButton(activity);
        loginButton.setReadPermissions("email", "user_birthday", "public_profile",
                "user_friends", "user_about_me", "user_events", "user_location");
    }

    @Override public Observable<AccessToken> get() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null)
            return Observable.just(accessToken);
        return createLoginObservable();
    }

    private Observable<AccessToken> createLoginObservable() {
        return Observable.create(subscriber -> {
            loginButton.registerCallback(FacebookCallbackManagerInstance.get(), new FacebookCallback<LoginResult>() {
                @Override public void onSuccess(LoginResult loginResult) {
                    subscriber.onNext(loginResult.getAccessToken());
                    subscriber.onCompleted();
                }

                @Override public void onCancel() {
                    subscriber.onError(new CancelFacebookLoginException());
                }

                @Override public void onError(FacebookException error) {
                    subscriber.onError(error);
                }
            });
            loginButton.performClick();
        });
    }

}
