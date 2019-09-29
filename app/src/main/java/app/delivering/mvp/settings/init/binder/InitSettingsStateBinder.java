package app.delivering.mvp.settings.init.binder;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import app.R;
import app.core.settings.cache.common.entity.SettingsModel;
import app.delivering.component.settings.SettingsFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.settings.init.presenter.InitSettingsStatePresenter;
import app.delivering.mvp.settings.init.presenter.SaveToCacheStateInterface;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class InitSettingsStateBinder extends BaseBinder {
    @BindView(R.id.settings_container) View container;
    @BindView(R.id.auto_open_tab_switcher) SwitchCompat autoOpen;
    @BindView(R.id.facebook_visibility_switcher) SwitchCompat visibility;
    @BindView(R.id.version) TextView version;
    private final InitSettingsStatePresenter settingsStatePresenter;
    private final SaveToCacheStateInterface saveToCacheStateInterface;

    public InitSettingsStateBinder(SettingsFragment fragment) {
        super(fragment.getBaseActivity());
        settingsStatePresenter = new InitSettingsStatePresenter(getActivity());
        saveToCacheStateInterface = settingsStatePresenter;
    }

    @Override public void afterViewsBounded() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        settingsStatePresenter.process()
                .subscribe(this::setSettingsState, e->{});
    }

    private void setSettingsState(SettingsModel settingsModel) {
        visibility.setChecked(settingsModel.isFacebookVisibility());
        autoOpen.setChecked(settingsModel.isAutoOpenTab());
    }

    @OnClick(R.id.settings_container) void screenClicks(){}

    @OnCheckedChanged(R.id.auto_open_tab_switcher) void onAutoOpenStates(boolean isChecked) {
        saveToCacheStateInterface.save(isChecked)
                .subscribe(value -> {}, e-> {}, ()->{});
    }
}
