package app.core.uber.tobar.entity;


import com.google.android.gms.location.places.Place;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;

public class OrderRideToBarResponse {
    private BarModel barModel;
    private List<Place> places;

    public void setBarModel(BarModel barModel) {
        this.barModel = barModel;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public BarModel getBarModel() {
        return barModel;
    }

    public List<Place> getPlaces() {
        return places;
    }
}
