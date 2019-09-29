package app.core.login.facebook.interactor;


import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import app.core.BaseOutputInteractor;
import app.core.gcm.interactor.PutNotificationTokenInteractor;
import app.core.login.facebook.entity.FacebookProfileResponse;
import app.core.login.facebook.entity.LoginRequest;
import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.entity.SignUpRequest;
import app.core.login.facebook.entity.Younger21Exception;
import app.core.login.facebook.gateway.FacebookLoginGateway;
import app.core.login.facebook.gateway.LoginGateway;
import app.core.login.facebook.gateway.SignUpGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.account.PutAndroidAccountGateway;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.facebook.login.FacebookButtonLoginGateway;
import app.gateway.facebook.profile.FacebookGraphProfileGateway;
import app.gateway.login.RetrofitLoginGateway;
import app.gateway.logout.LogoutAndroidAccountGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.signup.RetrofitSignUpGateway;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LoginFacebookInteractor implements BaseOutputInteractor<Observable<Bundle>> {
    private final FacebookLoginGateway facebookLoginGateway;
    private final LoginGateway loginGateway;
    private final SignUpGateway signUpGateway;
    private final PermissionInteractor permissionInteractor;
    private final FacebookGraphProfileGateway facebookProfileGateway;
    private final LogoutAndroidAccountGateway logoutAndroidAccountGateway;
    private final PutAndroidAccountGateway putAndroidAccountGateway;
    private final PutNotificationTokenInteractor putNotificationTokenInteractor;

    public LoginFacebookInteractor(BaseActivity activity) {
        loginGateway = new RetrofitLoginGateway();
        facebookLoginGateway = new FacebookButtonLoginGateway(activity);
        signUpGateway = new RetrofitSignUpGateway();
        permissionInteractor = new PermissionInteractor(activity);
        facebookProfileGateway = new FacebookGraphProfileGateway();
        logoutAndroidAccountGateway = new LogoutAndroidAccountGateway(activity);
        putAndroidAccountGateway = new PutAndroidAccountGateway(activity);
        putNotificationTokenInteractor = new PutNotificationTokenInteractor(activity);
    }

    @Override
    public Observable<Bundle> process() {
        return permissionInteractor.process()
                .concatMap(isGranted -> MixpanelSendGateway.send(MixpanelEvents.getLoginEvent(MixpanelEvents.TYPE_FACEBOOK)))
                .concatMap(isGranted -> facebookLoginGateway.get())
                .observeOn(Schedulers.io())
                .concatMap(this::checkAge)
                .concatMap(this::postLogin)
                .concatMap(this::putUserId)
                .onErrorResumeNext(this::handleNewUser)
                .concatMap(this::logout)
                .concatMap(putAndroidAccountGateway::put)
                .concatMap(this::putNotification)
                .concatMap(this::sendAnalytics);
    }

    private Observable<Bundle> putNotification(Bundle bundle) {
        return putNotificationTokenInteractor.process()
                .concatMap(response -> Observable.just(bundle));
    }

    private Observable<? extends AccessToken> checkAge(AccessToken accessToken) {
        return facebookProfileGateway.get()
                .doOnNext(this::checkYounger21)
                .concatMap(this::checkProfileResponse)
                .concatMap(profile -> Observable.just(accessToken));
    }

    private Observable<FacebookProfileResponse> checkProfileResponse(FacebookProfileResponse facebookProfileResponse) {
        return Observable.just(QorumSharedCache.checkFBPhoto().save(BaseCacheType.BOOLEAN, facebookProfileResponse.getPictureData().getPicture().isSilhouette()))
                .map(isResponse -> facebookProfileResponse);
    }

    private void checkYounger21(FacebookProfileResponse profile) {
        if (profile.getAgeRange() != null && isYounger21(profile)) {
            LoginManager.getInstance().logOut();
            throw new Younger21Exception();
        }
    }

    private boolean isYounger21(FacebookProfileResponse profile) {
        return profile.getAgeRange().getMin() < 21;
    }

    private Observable<LoginResponse> postLogin(AccessToken accessToken) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setFacebookToken(accessToken.getToken());
        return loginGateway.post(loginRequest);
    }

    private Observable<LoginResponse> putUserId(LoginResponse loginResponse) {
        return Observable.just(QorumSharedCache.checkUserId().save(BaseCacheType.LONG, loginResponse.getId()))
                .concatMap(id -> Observable.just(loginResponse));
    }

    private Observable<LoginResponse> logout(LoginResponse loginResponse) {
        return logoutAndroidAccountGateway.logout()
                .concatMap(o -> Observable.just(loginResponse));
    }

    private Observable<LoginResponse> handleNewUser(Throwable throwable) {
        if (isUserNew(throwable))
            return hiddenSignUp()
                    .concatMap(this::putUserId);
        else
            return Observable.error(throwable);
    }

    private boolean isUserNew(Throwable throwable) {
        return throwable instanceof HttpException && ((HttpException) throwable).code() == 401;
    }

    private Observable<LoginResponse> hiddenSignUp() {
        SignUpRequest signUpRequest = new SignUpRequest();
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        String token = currentAccessToken.getToken();
        signUpRequest.setFacebookToken(token);
        return facebookProfileGateway.get()
                .concatMap(this::sendRegistrationLog)
                .concatMap(isOk -> signUpGateway.post(signUpRequest));
    }

    private Observable<Boolean> sendRegistrationLog(FacebookProfileResponse response) {
        return MixpanelSendGateway.send(MixpanelEvents.getRegistrationEvent(MixpanelEvents.TYPE_FACEBOOK,
                response.getGender(),
                "",
                response.getBirthday()));
    }

    private Observable<? extends Bundle> sendAnalytics(Bundle bundle) {
        return MixpanelSendGateway.send(MixpanelEvents.getLogonEvent(false))
                .map(isOk -> bundle);
    }
}
