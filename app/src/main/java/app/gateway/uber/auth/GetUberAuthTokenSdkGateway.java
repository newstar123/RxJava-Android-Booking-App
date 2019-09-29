package app.gateway.uber.auth;


import android.content.Intent;
import android.support.annotation.NonNull;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.core.auth.LoginCallback;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.AccessTokenSession;

import java.util.Set;

import app.core.uber.auth.entity.UberAuthException;
import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.auth.gateway.GetUberAuthTokenGateway;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.auth.RedirectAuthActivity;
import app.gateway.ride.token.register.RegisterRideAuthRequest;
import app.gateway.ride.token.register.RegisterRideAuthResponse;
import app.gateway.ride.token.register.RegisterRideAuthResponseBody;
import app.gateway.ride.token.register.RegisterRideAuthRestGateway;
import app.gateway.uber.UberLoginManagerInstance;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetUberAuthTokenSdkGateway implements GetUberAuthTokenGateway {
    private final AccessTokenManager accessTokenManager;
    private final RegisterRideAuthRestGateway registerRideAuthRestGateway;
    private BaseActivity activity;

    public GetUberAuthTokenSdkGateway(BaseActivity activity) {
        this.activity = activity;
        accessTokenManager = new AccessTokenManager(activity);
        registerRideAuthRestGateway = new RegisterRideAuthRestGateway(activity);
    }

    @Override public Observable<UberAuthToken> get() {
        AccessToken accessToken = accessTokenManager.getAccessToken();
        if (accessToken != null)
            return Observable.just(createUberAuthToken(accessToken));
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(Subscriber<? super String> subscriber) {
                LoginCallback loginCallback = new LoginCallback() {
                    @Override
                    public void onLoginCancel() {
                        subscriber.onError(new UberAuthException(AuthenticationError.CANCELLED));
                    }

                    @Override
                    public void onLoginError(@NonNull AuthenticationError error) {
                        subscriber.onError(new UberAuthException(error));
                    }

                    @Override
                    public void onLoginSuccess(@NonNull AccessToken accessToken) {

                    }

                    @Override public void onAuthorizationCodeReceived(@NonNull String authorizationCode) {
                        subscriber.onNext(authorizationCode);
                        subscriber.onCompleted();
                    }
                };
                LoginManager loginManager = new LoginManager(accessTokenManager, loginCallback);
                UberLoginManagerInstance.setLoginManager(loginManager);
                loginManager.setRedirectForAuthorizationCode(true);
                Intent intent = new Intent(activity, RedirectAuthActivity.class);
                activity.startActivityForResult(intent, 1001);
            }
        }).concatMap(this::registerAuthToken)
                .map(this::createAuthAccessToken)
                .map(this::createUberAuthToken)
                .subscribeOn(Schedulers.io());
    }

    private Observable<RegisterRideAuthResponse> registerAuthToken(String authorizationCode) {
        RegisterRideAuthRequest request = new RegisterRideAuthRequest(authorizationCode);
        return registerRideAuthRestGateway.post(request);
    }

    private AccessToken createAuthAccessToken(RegisterRideAuthResponse registerResponse){
        RegisterRideAuthResponseBody resp = registerResponse.getResp();
        Set<Scope> scopes = Scope.parseScopes(resp.getScope());
        String token = resp.getAccessToken();
        String refreshToken = resp.getRefreshToken();
        String tokenType = resp.getTokenType();
        long expirationTime = 2592000;
        return new AccessToken(expirationTime, scopes, token, refreshToken, tokenType);
    }

    private UberAuthToken createUberAuthToken(AccessToken accessToken) {
        AccessTokenManager accessTokenManager = new AccessTokenManager(activity);
        accessTokenManager.setAccessToken(accessToken);
        AccessTokenSession accessTokenSession = new AccessTokenSession(UberSdk.getDefaultSessionConfiguration(),
                accessTokenManager);
        UberAuthToken uberAuthToken = new UberAuthToken();
        uberAuthToken.setSession(accessTokenSession);
        return uberAuthToken;
    }


}
