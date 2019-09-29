package app.delivering.mvp.ride.order.type.apply.model;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.core.uber.product.entity.UberProductsResponse;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;

public class ApplyTypesModel {
   private List<RideCategory> rideWithCategories;
   private UberProductsResponse products;
    private List<LatLng> route;

    public List<RideCategory> getRideWithCategories() {
        return rideWithCategories;
    }

    public void setRideWithCategories(List<RideCategory> rideWithCategories) {
        this.rideWithCategories = rideWithCategories;
    }

    public UberProductsResponse getProducts() {
        return products;
    }

    public void setProducts(UberProductsResponse response) {
        this.products = response;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public List<LatLng> getRoute() {
        return route;
    }
}
