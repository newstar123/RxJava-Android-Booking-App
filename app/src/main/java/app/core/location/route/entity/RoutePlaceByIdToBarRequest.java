package app.core.location.route.entity;


import com.google.android.gms.maps.model.LatLng;

public class RoutePlaceByIdToBarRequest {
    private LatLng departure;
    private LatLng destination;

    public LatLng getDeparture() {
        return departure;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }
}
