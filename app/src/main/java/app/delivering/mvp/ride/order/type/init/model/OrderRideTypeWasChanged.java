package app.delivering.mvp.ride.order.type.init.model;


import app.delivering.mvp.ride.order.fare.apply.model.RideType;

public class OrderRideTypeWasChanged {
    private RideType rideType;
    private String categoryName;

    public OrderRideTypeWasChanged(RideType rideType) {
        this.rideType = rideType;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
