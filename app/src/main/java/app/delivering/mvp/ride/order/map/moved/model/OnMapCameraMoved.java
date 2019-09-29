package app.delivering.mvp.ride.order.map.moved.model;


import com.google.android.gms.maps.model.LatLng;

public class OnMapCameraMoved {
    private LatLng target;

    public OnMapCameraMoved(LatLng target) {
        this.target = target;
    }

    public LatLng getTarget() {
        return target;
    }
}
