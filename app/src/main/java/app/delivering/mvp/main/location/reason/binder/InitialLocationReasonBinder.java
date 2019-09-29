package app.delivering.mvp.main.location.reason.binder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.main.location.InitialLocationReasonActivity;
import app.delivering.mvp.BaseBinder;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class InitialLocationReasonBinder extends BaseBinder {
    @BindView(R.id.initial_location_reason_button) Button reasonButton;
    @BindView(R.id.initial_location_reason_message) TextView reasonMessage;
    @BindView(R.id.initial_location_reason_title) TextView reasonTitle;
    private boolean isCheckGPSResultCanceled;
    private final boolean shouldProvidegeAppSettings;

    public InitialLocationReasonBinder(BaseActivity activity) {
        super(activity);
        isCheckGPSResultCanceled = activity.getIntent().getBooleanExtra(InitialLocationReasonActivity.IS_CHECK_GPS_RESULT_CANCELED, false);
        shouldProvidegeAppSettings = activity.getIntent().getBooleanExtra(InitialLocationReasonActivity.provideToAppSettings, false);
    }

    @Override
    public void afterViewsBounded() {
        reasonTitle.setVisibility(isCheckGPSResultCanceled ? View.VISIBLE : View.GONE);
        if (isCheckGPSResultCanceled) {
            reasonButton.setText(R.string.location_reason_button);
            reasonMessage.setText(R.string.location_reason_message);
        }
        if (shouldProvidegeAppSettings)
            reasonButton.setText(getString(R.string.location_reason_button));
    }

    @OnClick(R.id.initial_location_reason_button)
    void onReasonShowed() {
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getLocationPermissionExplanationUnderstoodEvent());
        if (shouldProvidegeAppSettings)
            provideAppSettings();
        else if (isCheckGPSResultCanceled)
            getActivity().finish();
        else
            Observable.just(QorumSharedCache.checkLocationReason().save(BaseCacheType.BOOLEAN, true))
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(isOk -> finish(), e -> { }, ()-> { });
    }

    private void provideAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        finish();
    }

    private void finish() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
