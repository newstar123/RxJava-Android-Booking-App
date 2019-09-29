package app.delivering.mvp.ride.order.init.tobar.binder.subbinder;


import android.support.v4.view.ViewPager;

import app.R;
import app.delivering.component.BaseActivity;

public class ViewPagerTypeChangeSubBinder {
    private final BaseActivity activity;

    public ViewPagerTypeChangeSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public void apply() {
        ViewPager orderRideTypeViewPager = (ViewPager) activity.findViewById(R.id.order_ride_type_pager);
        OnRideTypePagerChange onRideTypePagerChange = new OnRideTypePagerChange(orderRideTypeViewPager);
        orderRideTypeViewPager.addOnPageChangeListener(onRideTypePagerChange);
    }
}
