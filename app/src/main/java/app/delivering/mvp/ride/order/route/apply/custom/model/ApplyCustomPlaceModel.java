package app.delivering.mvp.ride.order.route.apply.custom.model;

import com.google.android.gms.location.places.Place;

public class ApplyCustomPlaceModel {
    private Place place;
    private boolean isDeparture;
    private Place departure;
    private Place destination;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public boolean isDeparture() {
        return isDeparture;
    }

    public void departure(boolean departure) {
        isDeparture = departure;
    }


    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public void setDeparture(Place departure) {
        this.departure = departure;
    }

    public Place getDeparture() {
        return departure;
    }

}
