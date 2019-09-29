package app.delivering.mvp.ride.order.type.init.binder.event;

import android.view.View;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;

import app.delivering.component.BaseFragment;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.init.model.OrderRideTypeWasChanged;

public class OrderRideTypeChangedEvent {
    private BaseFragment fragment;

    public OrderRideTypeChangedEvent(BaseFragment fragment) {
        this.fragment = fragment;
    }

    public void send(RadioGroup group, int checkedId) {
        View viewById = group.findViewById(checkedId);
        if (viewById != null) {
            RideType rideType = (RideType) viewById.getTag();
            OrderRideTypeWasChanged event = new OrderRideTypeWasChanged(rideType);
            String categoryName = fragment.getArguments().getString(OrderRideTypeFragment.ORDER_TYPE_ID);
            event.setCategoryName(categoryName);
            EventBus.getDefault().post(event);
        }
    }
}
