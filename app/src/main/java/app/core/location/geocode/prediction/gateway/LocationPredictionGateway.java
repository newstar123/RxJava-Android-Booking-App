package app.core.location.geocode.prediction.gateway;


import app.core.location.geocode.prediction.entity.PlacePredictionRequest;
import app.core.location.geocode.prediction.entity.PlacePredictionResponse;
import rx.Observable;

public interface LocationPredictionGateway {
    Observable<PlacePredictionResponse> get(PlacePredictionRequest request);
}
