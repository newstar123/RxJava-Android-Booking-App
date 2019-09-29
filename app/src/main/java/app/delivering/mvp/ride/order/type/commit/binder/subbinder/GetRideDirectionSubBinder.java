package app.delivering.mvp.ride.order.type.commit.binder.subbinder;


import app.core.uber.start.entity.RideDirection;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.ride.order.init.type.InitialRideType;

public class GetRideDirectionSubBinder {
    private BaseActivity activity;

    public GetRideDirectionSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public String getRideDirection() {
        switch (getRideType()){
            case FROM_THE_VENUE:
                return RideDirection.FROM_BAR.toString();
            case CUSTOM:
                return RideDirection.FROM_BAR.toString();
            default:
                return RideDirection.TO_BAR.toString();
        }
    }

    private InitialRideType getRideType() {
        int value = activity.getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }
}
