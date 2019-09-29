package app.core.uber.product.entity;


import com.google.android.gms.maps.model.LatLng;

public class UberProductRequest {
    private LatLng departure;

    public LatLng getDeparture() {
        return departure;
    }

    public void setDeparture(LatLng departure) {
        this.departure = departure;
    }
}
