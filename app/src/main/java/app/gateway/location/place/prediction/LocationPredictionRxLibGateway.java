package app.gateway.location.place.prediction;

import android.location.Location;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import app.CustomApplication;
import app.core.location.geocode.prediction.entity.PlacePrediction;
import app.core.location.geocode.prediction.entity.PlacePredictionRequest;
import app.core.location.geocode.prediction.entity.PlacePredictionResponse;
import app.core.location.geocode.prediction.gateway.LocationPredictionGateway;
import app.delivering.mvp.ride.order.address.edit.exceptions.UnSuccessPredictionException;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.schedulers.Schedulers;


public class LocationPredictionRxLibGateway implements LocationPredictionGateway {
    private final ReactiveLocationProvider locationProvider;
    private final static int MAX_RADIUS = 50000;

    public LocationPredictionRxLibGateway() {
        locationProvider = new ReactiveLocationProvider(CustomApplication.get());
    }

    @Override public Observable<PlacePredictionResponse> get(PlacePredictionRequest request) {
        String query = request.getQuery();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        LatLngBounds latLngBounds = toBounds(request.getLocation());
        return locationProvider.getPlaceAutocompletePredictions(query, latLngBounds , typeFilter)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::checkStatusSuccess)
                .flatMap(this::convert);
    }

    private Observable<PlacePredictionResponse> convert(AutocompletePredictionBuffer autocompletePredictions) {
        return Observable.from(autocompletePredictions)
                .map(this::createAddress)
                .toList()
                .map(PlacePredictionResponse::new)
                .doOnNext(result -> autocompletePredictions.release());
    }

    private PlacePrediction createAddress(AutocompletePrediction prediction) {
        return new PlacePrediction(prediction.getFullText(null).toString(),
                prediction.getPlaceId(),
                prediction.getPrimaryText(null).toString(),
                prediction.getSecondaryText(null).toString());
    }

    private void checkStatusSuccess(AutocompletePredictionBuffer autocompletePredictions) {
        if (!autocompletePredictions.getStatus().isSuccess()) {
            autocompletePredictions.release();
            throw new UnSuccessPredictionException();
        }
    }

    private LatLngBounds toBounds(Location centerLocation) {
        LatLng latLng = new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
        LatLng southwest = SphericalUtil.computeOffset(latLng, MAX_RADIUS * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(latLng, MAX_RADIUS * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
}
