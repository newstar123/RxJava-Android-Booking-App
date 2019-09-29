package app.core.location.route.entity;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RoutePlaceByIdToBarResponse {
    private List<LatLng> route;

    public RoutePlaceByIdToBarResponse(List<LatLng> route) {
        this.route = route;
    }

    public List<LatLng> getRoute() {
        return route;
    }

}
