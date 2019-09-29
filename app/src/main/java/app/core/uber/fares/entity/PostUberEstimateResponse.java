package app.core.uber.fares.entity;

import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.rides.client.model.RideEstimate;

import app.core.uber.product.entity.UberProductResponse;

public class PostUberEstimateResponse {
    private RideEstimate rideEstimate;
    private UberProductResponse product;
    private long timestamp;
    private LatLng departure;
    private LatLng destination;

    public void setRideEstimate(RideEstimate rideEstimate) {
        this.rideEstimate = rideEstimate;
    }

    public void setProduct(UberProductResponse product) {
        this.product = product;
    }

    public RideEstimate getRideEstimate() {
        return rideEstimate;
    }

    public UberProductResponse getProduct() {
        return product;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public LatLng getDeparture() {
        return departure;
    }

    public LatLng getDestination() {
        return destination;
    }
}
