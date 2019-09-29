package app.delivering.mvp.network.binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.component.network.NoInternetConnectionDialog;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.network.events.InternetConnectedEvent;
import app.delivering.mvp.network.events.InternetErrorConnectionEvent;

public class BlockByNetworkStateDialogBinder extends BaseBinder {
    private NoInternetConnectionDialog dialog;

    public BlockByNetworkStateDialogBinder(BaseActivity activity) {
        super(activity);
        dialog = new NoInternetConnectionDialog(activity, android.R.style.Theme_Translucent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getNoConnectionEvent(InternetErrorConnectionEvent event) {
        EventBus.getDefault().removeStickyEvent(InternetErrorConnectionEvent.class);
        dialog.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getNetworkConnectedEvent(InternetConnectedEvent event) {
        EventBus.getDefault().removeStickyEvent(InternetConnectedEvent.class);
        dialog.dismiss();
    }
}
