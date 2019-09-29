package app.delivering.mvp.ride.order.fare.apply.events;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.core.uber.product.entity.UberProductsResponse;

public class ApplyFareEvent {
    private UberProductsResponse products;
    private List<LatLng> route;

    public UberProductsResponse getProducts() {
        return products;
    }

    public void setProducts(UberProductsResponse products) {
        this.products = products;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }
}
