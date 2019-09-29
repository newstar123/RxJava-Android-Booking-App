package app.delivering.component.verify;

import android.os.Bundle;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.verify.binder.actionbar.VerifyPhoneActionBarBinder;
import app.delivering.mvp.verify.binder.init.VerifyPhoneInitBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;

public class VerifyPhoneNumberActivity extends BaseActivity {
    public static final int PHONE_VERIFICATION_REQUEST = 10525;
    public static final int PHONE_VERIFICATION_REQUEST_FOR_UBER = 10524;
    public static final int PHONE_VERIFICATION_EMPTY_RESULT = 10000;
    public static final String PHONE_NUMBER = "app.delivering.component.verify.PHONE_NUMBER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone_number_activity);
        initViews();

        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getPhoneAskedEvent());
    }

    private void initViews() {
        VerifyPhoneInitBinder initBinder = new VerifyPhoneInitBinder(this);
        addItemForViewsInjection(initBinder);
        VerifyPhoneActionBarBinder verifyPhoneActionBarBinder = new VerifyPhoneActionBarBinder(this);
        addItemForViewsInjection(verifyPhoneActionBarBinder);
    }

    @Override
    public void onBackPressed() {
        setResult(PHONE_VERIFICATION_EMPTY_RESULT);
        finish();
    }
}
