package app.delivering.mvp.profile.drawer.close.binder;

import android.support.v4.widget.DrawerLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.drawer.init.events.CloseNavigationDrawerEvent;
import butterknife.BindView;

public class CloseNavigationDrawerBinder extends BaseBinder {
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    public CloseNavigationDrawerBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeDrawer(CloseNavigationDrawerEvent event) {
        if (drawerLayout != null)
            drawerLayout.closeDrawers();
    }
}
