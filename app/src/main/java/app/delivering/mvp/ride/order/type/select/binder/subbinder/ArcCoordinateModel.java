package app.delivering.mvp.ride.order.type.select.binder.subbinder;


import com.google.android.gms.maps.model.LatLng;

public class ArcCoordinateModel {
    private double fraction;
    private LatLng latLng;
    private double deviation;

    public void setFraction(double fraction) {
        this.fraction = fraction;
    }

    public double getFraction() {
        return fraction;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setDeviation(double fractionInRadian) {
        this.deviation = fractionInRadian;
    }

    public double getDeviation() {
        return deviation;
    }
}
