package app.delivering.mvp.ride.order.init.tobar.binder.subbinder;


import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.ride.order.pager.RideTypeAdapter;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.detail.show.model.OnRideTypeClicked;
import app.delivering.mvp.ride.order.type.init.binder.event.OrderRideTypeChangedEvent;

public class OnRideTypePagerChange implements ViewPager.OnPageChangeListener {
    private ViewPager orderRideTypeViewPager;

    public OnRideTypePagerChange(ViewPager orderRideTypeViewPager) {
        this.orderRideTypeViewPager = orderRideTypeViewPager;
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageSelected(int position) {
        int currentItem = orderRideTypeViewPager.getCurrentItem();
        BaseFragment item = (BaseFragment) ((RideTypeAdapter) orderRideTypeViewPager.getAdapter()).getItem(currentItem);
        if (item != null && item.getView() != null)
            changeRadioButton(item);
    }

    private void changeRadioButton(BaseFragment item) {
        RadioGroup radioGroup = item.getView().findViewById(R.id.ride_type_radio_group);
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        OrderRideTypeChangedEvent orderRideTypeChangedEvent = new OrderRideTypeChangedEvent(item);
        orderRideTypeChangedEvent.send(radioGroup, checkedRadioButtonId);
        View viewById = radioGroup.findViewById(checkedRadioButtonId);
        if (viewById != null && viewById.getTag() != null) {
            RideType rideType = (RideType) viewById.getTag();
            EventBus.getDefault().post(new OnRideTypeClicked(rideType));
        }
    }

    @Override public void onPageScrollStateChanged(int state) {

    }
}
