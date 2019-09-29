package app.delivering.mvp.ride.order.type.detail.show.model;


import app.delivering.mvp.ride.order.fare.apply.model.RideType;

public class OnRideTypeClicked {
    private RideType rideType;

    public OnRideTypeClicked(RideType rideType) {
        this.rideType = rideType;
    }

    public RideType getRideType() {
        return rideType;
    }
}
