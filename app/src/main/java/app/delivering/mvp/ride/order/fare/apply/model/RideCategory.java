package app.delivering.mvp.ride.order.fare.apply.model;


import java.util.List;

public class RideCategory {
    private String name;
    private List<RideType> rideTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RideType> getRideTypes() {
        return rideTypes;
    }

    public void setRideTypes(List<RideType> rideTypes) {
        this.rideTypes = rideTypes;
    }
}
