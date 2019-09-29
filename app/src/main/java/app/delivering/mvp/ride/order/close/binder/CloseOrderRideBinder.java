package app.delivering.mvp.ride.order.close.binder;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.close.events.CloseRideEvent;

public class CloseOrderRideBinder extends BaseBinder {

    public CloseOrderRideBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRideEvent(CloseRideEvent event) {
        getActivity().finish();
    }
}
