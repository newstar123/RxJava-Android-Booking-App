package app.delivering.mvp.ride.order.route.apply.custom.events;


import com.google.android.gms.location.places.Place;

public class ApplyPickUpAddressEvent {
    private Place model;

    public ApplyPickUpAddressEvent(Place model) {
        this.model = model;
    }

    public Place getModel() {
        return model;
    }
}
