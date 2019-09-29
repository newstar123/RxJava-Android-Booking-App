package app.core.location.geocode.prediction.interactor;


import android.location.Location;

import app.core.BaseInteractor;
import app.core.location.geocode.prediction.entity.PlacePredictionRequest;
import app.core.location.geocode.prediction.entity.PlacePredictionResponse;
import app.core.location.geocode.prediction.gateway.LocationPredictionGateway;
import app.core.location.get.GetRxLocationGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.location.current.GetCurrentRxLocationGateway;
import app.gateway.location.place.prediction.LocationPredictionRxLibGateway;
import rx.Observable;

public class LocationPredictionInteractor implements BaseInteractor<String, Observable<PlacePredictionResponse>> {
    private final GetRxLocationGateway getRxLocationGateway;
    private final PermissionInteractor permissionInteractor;
    private final LocationPredictionGateway locationPredictionGateway;

    public LocationPredictionInteractor(BaseActivity activity) {
        getRxLocationGateway = new GetCurrentRxLocationGateway(activity);
        permissionInteractor = new PermissionInteractor(activity);
        locationPredictionGateway = new LocationPredictionRxLibGateway();
    }

    @Override public Observable<PlacePredictionResponse> process(String query) {
        return permissionInteractor.process()
                .concatMap(isGranted -> getRxLocationGateway.get())
                .concatMap(location -> getPrediction(location, query));
    }

    private Observable<PlacePredictionResponse> getPrediction(Location location, String query) {
        PlacePredictionRequest placePredictionRequest = new PlacePredictionRequest();
        placePredictionRequest.setQuery(query);
        placePredictionRequest.setLocation(location);
        return locationPredictionGateway.get(placePredictionRequest);
    }
}
