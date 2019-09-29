package app.core.login.guest.interactor;


import android.os.Bundle;

import app.core.BaseOutputInteractor;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.account.PutAndroidAccountGateway;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import rx.Observable;

public class GuestLoginInteractor implements BaseOutputInteractor<Observable<Bundle>> {
    private final PutAndroidAccountGateway putAndroidAccountGateway;

    public GuestLoginInteractor(BaseActivity activity) {
        putAndroidAccountGateway = new PutAndroidAccountGateway(activity);
    }

    @Override public Observable<Bundle> process() {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(LoginResponse.GUEST_TOKEN);
        return putAndroidAccountGateway.put(loginResponse)
                .concatMap(this::sendAnalytics);
    }

    private Observable<? extends Bundle> sendAnalytics(Bundle bundle) {
        return MixpanelSendGateway.send(MixpanelEvents.getLogonEvent(true))
                .map(isOk -> bundle);
    }
}
