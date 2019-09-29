package app.core.location.current.entity;


import com.google.android.gms.location.places.Place;

import java.util.List;

public class CurrentPlaceResponse {
    private List<Place> places;

    public CurrentPlaceResponse(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }
}
