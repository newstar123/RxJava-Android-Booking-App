package app.delivering.mvp.ride.order.type.update.events;


import java.util.List;

import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;

public class UpdateTypeWithFareEvent {
    private List<RideCategory> rideCategories;

    public UpdateTypeWithFareEvent(List<RideCategory> rideCategories) {
        this.rideCategories = rideCategories;
    }

    public List<RideCategory> getRideCategories() {
        return rideCategories;
    }
}
