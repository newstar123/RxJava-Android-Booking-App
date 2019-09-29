package app.core.uber.fare.entity;


import com.google.android.gms.maps.model.LatLng;

import app.core.uber.product.entity.UberProductResponse;

public class PostUberEstimateRequest {
    private UberProductResponse product;
    private LatLng departure;
    private LatLng destination;
    private int capacity;

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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public UberProductResponse getProduct() {
        return product;
    }

    public void setProduct(UberProductResponse product) {
        this.product = product;
    }
}
