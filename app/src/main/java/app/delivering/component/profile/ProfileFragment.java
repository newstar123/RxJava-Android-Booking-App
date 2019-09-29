package app.delivering.component.profile;

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
import app.delivering.mvp.main.photo.autoopen.binder.AutoStartChangePhotoBinder;
import app.delivering.mvp.profile.drawer.init.events.EnableNavigationDrawerViewsEvent;
import app.delivering.mvp.profile.edit.actionbar.init.ProfileActionBarBinder;
import app.delivering.mvp.profile.edit.init.binder.ProfileInitBinder;
import app.delivering.mvp.profile.edit.init.events.OnStopProfileModelEvent;

public class ProfileFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        loadCityImage(view);
        return view;
    }

    private void initViews(View view) {
        ProfileActionBarBinder actionBarBinder = new ProfileActionBarBinder(getBaseActivity());
        addItemForViewsInjection(actionBarBinder, view);
        ProfileInitBinder profileInitBinder = new ProfileInitBinder(this);
        addToEventBusAndViewInjection(profileInitBinder, view);
        AutoStartChangePhotoBinder autoStartChangePhotoBinder = new AutoStartChangePhotoBinder(this);
        addItemForViewsInjection(autoStartChangePhotoBinder, view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override public void onStop() {
        EventBus.getDefault().post(new OnStopProfileModelEvent());
        EventBus.getDefault().postSticky(new EnableNavigationDrawerViewsEvent());
        super.onStop();
    }
}
