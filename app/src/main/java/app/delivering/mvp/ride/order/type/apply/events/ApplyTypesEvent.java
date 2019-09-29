package app.delivering.mvp.ride.order.type.apply.events;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ApplyTypesEvent {
    private List<LatLng> route;

    public ApplyTypesEvent(List<LatLng> route) {
        this.route = route;
    }

    public List<LatLng> getRoute() {
        return route;
    }
}
