package app.core.uber.fares.entity;


import com.google.android.gms.maps.model.LatLng;

import app.core.uber.product.entity.UberProductsResponse;

public class PostUberEstimatesRequest {
    private UberProductsResponse products;
    private LatLng departure;
    private LatLng destination;

    public void setProducts(UberProductsResponse products) {
        this.products = products;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public UberProductsResponse getProducts() {
        return products;
    }

    public LatLng getDeparture() {
        return departure;
    }

    public LatLng getDestination() {
        return destination;
    }
}
