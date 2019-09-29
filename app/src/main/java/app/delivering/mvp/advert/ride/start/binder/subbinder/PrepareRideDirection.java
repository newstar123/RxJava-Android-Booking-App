package app.delivering.mvp.advert.ride.start.binder.subbinder;

import app.core.uber.start.entity.RideDirection;
import app.delivering.component.BaseActivity;


public class PrepareRideDirection {
    public static RideDirection get(BaseActivity activity, String key) {
        String rideDirectionString = activity.getIntent()
                .getStringExtra(key)
                .toUpperCase();
        return RideDirection.valueOf(rideDirectionString);
    }
}
