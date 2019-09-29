package app.delivering.component.main.location;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.location.reason.binder.InitialLocationReasonBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;

public class InitialLocationReasonActivity extends BaseActivity {

    public static final String provideToAppSettings = "InitialLocationReasonActivity.provideToAppSettings";
    public static final String IS_CHECK_GPS_RESULT_CANCELED = "is_check_GPS_result_canceled";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_location_reason);
        initUseCases();

        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getLocationPermissionExplanationEvent());
    }

    private void initUseCases() {
        InitialLocationReasonBinder binder = new InitialLocationReasonBinder(this);
        addItemForViewsInjection(binder);
    }

    @Override
    public void onBackPressed() {
    }
}
