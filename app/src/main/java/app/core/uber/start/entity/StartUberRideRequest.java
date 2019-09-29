package app.core.uber.start.entity;


import com.google.android.gms.maps.model.LatLng;

public class StartUberRideRequest {
    private String fareId;
    private String productId;
    private LatLng departure;
    private LatLng destination;
    private String departureAddress;
    private String destinationAddress;
    private RideDirection rideDirection;
    private int capacity;
    private double discount;
    private long fareExpiredAt;

    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }

    public LatLng getDeparture() {
        return departure;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public LatLng getDestination() {
        return destination;
    }

    public RideDirection getRideDirection() {
        return rideDirection;
    }

    public void setRideDirection(RideDirection rideDirection) {
        this.rideDirection = rideDirection;
    }

    public String getDepartureAddress() {
        return departureAddress;
    }

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = departureAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setFareExpiredAt(long fareExpiredAt) {
        this.fareExpiredAt = fareExpiredAt;
    }

    public long getFareExpiredAt() {
        return fareExpiredAt;
    }
}
