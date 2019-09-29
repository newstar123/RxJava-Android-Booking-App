package app.delivering.component.verify;

import android.os.Bundle;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.verify.binder.actionbar.VerifyEmailActionBarBinder;
import app.delivering.mvp.verify.binder.init.VerifyEmailInitBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;

public class VerifyEmailActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_email_activity);
        initViews();
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getEmailVerificationAskedEvent());
    }

    private void initViews() {
        VerifyEmailInitBinder initBinder = new VerifyEmailInitBinder(this);
        addItemForViewsInjection(initBinder);
        VerifyEmailActionBarBinder verifyEmailActionBarBinder = new VerifyEmailActionBarBinder(this);
        addItemForViewsInjection(verifyEmailActionBarBinder);
    }
}
