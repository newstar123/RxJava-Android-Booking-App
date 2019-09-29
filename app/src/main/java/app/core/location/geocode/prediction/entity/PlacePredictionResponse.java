package app.core.location.geocode.prediction.entity;


import java.util.List;

public class PlacePredictionResponse {
    private List<PlacePrediction> result;

    public PlacePredictionResponse(List<PlacePrediction> result) {
        this.result = result;
    }

    public List<PlacePrediction> getResult() {
        return result;
    }
}
