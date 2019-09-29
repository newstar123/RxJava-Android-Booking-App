package app.delivering.mvp.main.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Button;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class InitialAccountReasonBinder extends BaseBinder {
    @BindView(R.id.initial_account_reason_button) Button gotItButton;
    public static final String provideToAppSettings = "InitialAccountReasonBinder.provideToAppSettings";
    private final boolean shouldProvideAppSettings;

    public InitialAccountReasonBinder(BaseActivity activity) {
        super(activity);
        shouldProvideAppSettings = activity.getIntent().getBooleanExtra(provideToAppSettings, false);
    }

    @Override
    public void afterViewsBounded() {
        gotItButton.setText(shouldProvideAppSettings ? getString(R.string.location_reason_button) : getString(R.string.got_it));
    }

    @OnClick(R.id.initial_account_reason_button)
    void onReasonShowed() {
        if (shouldProvideAppSettings) {
            provideAppSettings();
        } else
            Observable.just(true)
                    .map(value -> QorumSharedCache.checkSharedAccount().save(BaseCacheType.BOOLEAN, value))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isOk -> finish(), e -> { }, () -> { });
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
