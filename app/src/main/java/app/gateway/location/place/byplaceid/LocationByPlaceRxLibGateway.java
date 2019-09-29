package app.gateway.location.place.byplaceid;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import app.CustomApplication;
import app.core.location.route.entity.UnSuccessPlaceByIdException;
import app.core.location.route.gateway.LocationByPlaceIdGateway;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.schedulers.Schedulers;


public class LocationByPlaceRxLibGateway implements LocationByPlaceIdGateway {
    private final ReactiveLocationProvider locationProvider;

    public LocationByPlaceRxLibGateway() {
        locationProvider = new ReactiveLocationProvider(CustomApplication.get());
    }

    @Override public Observable<Place> get(String placeId) {
        return locationProvider.getPlaceById(placeId)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::checkStatusSuccess)
                .map(this::convert);
    }

    private void checkStatusSuccess(PlaceBuffer placeBuffer) {
        if (!placeBuffer.getStatus().isSuccess()) {
            placeBuffer.release();
            throw new UnSuccessPlaceByIdException();
        }
    }

    private Place convert(PlaceBuffer placeBuffer) {
        Place place = placeBuffer.get(0);
        Place freeze = place.freeze();
        placeBuffer.release();
        return freeze;
    }

}
