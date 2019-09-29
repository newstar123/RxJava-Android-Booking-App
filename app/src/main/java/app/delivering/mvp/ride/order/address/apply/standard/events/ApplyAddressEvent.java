package app.delivering.mvp.ride.order.address.apply.standard.events;


import com.google.android.gms.location.places.Place;

public class ApplyAddressEvent {
    private Place model;

    public ApplyAddressEvent(Place model) {
        this.model = model;
    }

    public Place getModel() {
        return model;
    }
}
