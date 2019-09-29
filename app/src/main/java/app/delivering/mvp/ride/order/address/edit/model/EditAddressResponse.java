package app.delivering.mvp.ride.order.address.edit.model;


import java.util.List;

import app.core.location.geocode.prediction.entity.PlacePrediction;

public class EditAddressResponse {
    private List<PlacePrediction> result;

    public void setResult(List<PlacePrediction> result) {
        this.result = result;
    }

    public List<PlacePrediction> getResult() {
        return result;
    }
}
