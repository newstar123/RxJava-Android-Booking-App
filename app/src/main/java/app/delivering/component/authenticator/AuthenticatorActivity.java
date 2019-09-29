package app.delivering.component.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.mvp.authenticator.init.binder.AuthenticatorInitBinder;
import app.delivering.mvp.authenticator.init.events.OnFinishAuthenticatorActivityEvent;
import app.delivering.mvp.authenticator.init.events.OnStartAuthenticatorActivityEvent;
import app.delivering.mvp.authenticator.login.facebook.binder.FacebookLoginBinder;
import app.delivering.mvp.authenticator.login.guest.binder.GuestLoginBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.facebook.FacebookCallbackManagerInstance;
import app.gateway.location.settings.GoogleLocationSettingsGateway;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getEnteredToLoginScreenEvent());

        setContentView(R.layout.activity_authenticator);
        initUseCases();
    }

    private void initUseCases() {
        AuthenticatorInitBinder authenticatorInitBinder = new AuthenticatorInitBinder(this);
        addToEventBusAndViewInjection(authenticatorInitBinder);
        FacebookLoginBinder facebookLoginBinder = new FacebookLoginBinder(this);
        addToEventBusAndViewInjection(facebookLoginBinder);
        GuestLoginBinder guestLoginBinder = new GuestLoginBinder(this);
        addItemForViewsInjection(guestLoginBinder);
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new OnStartAuthenticatorActivityEvent());
        loadCityImage();
    }

    @Override public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookCallbackManagerInstance.get().onActivityResult(requestCode, resultCode, data);
        if (GoogleLocationSettingsGateway.CHECK_GPS_ACTIVE == requestCode && resultCode == Activity.RESULT_OK)
            EventBus.getDefault().post(new OnStartAuthenticatorActivityEvent());
    }

    @Override public void finish() {
        EventBus.getDefault().postSticky(new OnFinishAuthenticatorActivityEvent());
        super.finish();
    }
}
