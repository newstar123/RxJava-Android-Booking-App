package app.delivering.component.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.settings.appbar.init.SettingsActionBarBinder;
import app.delivering.mvp.settings.facebook.binder.FacebookVisibilityBinder;
import app.delivering.mvp.settings.init.binder.InitSettingsStateBinder;
import app.delivering.mvp.settings.init.events.DestroySettingsEvent;

public class SettingsFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        loadCityImage(view);
        return view;
    }

    private void initViews(View view) {
        SettingsActionBarBinder actionBarBinder = new SettingsActionBarBinder(getBaseActivity());
        addItemForViewsInjection(actionBarBinder, view);
        InitSettingsStateBinder initSettingsStateBinder = new InitSettingsStateBinder(this);
        addItemForViewsInjection(initSettingsStateBinder, view);
        FacebookVisibilityBinder facebookVisibilityBinder = new FacebookVisibilityBinder(getBaseActivity());
        addItemForViewsInjection(facebookVisibilityBinder, view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override public void onDestroy() {
        EventBus.getDefault().postSticky(new DestroySettingsEvent());
        super.onDestroy();
    }
}
