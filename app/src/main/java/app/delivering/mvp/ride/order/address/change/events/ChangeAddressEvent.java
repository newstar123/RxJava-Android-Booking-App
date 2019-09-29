package app.delivering.mvp.ride.order.address.change.events;


import app.core.location.geocode.prediction.entity.PlacePrediction;

public class ChangeAddressEvent {
    private PlacePrediction model;

    public ChangeAddressEvent(PlacePrediction model) {
        this.model = model;
    }

    public PlacePrediction getModel() {
        return model;
    }
}
