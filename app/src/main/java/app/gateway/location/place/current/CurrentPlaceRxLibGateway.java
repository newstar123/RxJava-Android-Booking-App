package app.gateway.location.place.current;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import java.util.List;

import app.CustomApplication;
import app.core.location.current.gateway.CurrentPlaceGateway;
import app.delivering.mvp.ride.order.address.edit.exceptions.UnSuccessPredictionException;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.schedulers.Schedulers;


public class CurrentPlaceRxLibGateway implements CurrentPlaceGateway {
    private final ReactiveLocationProvider locationProvider;

    public CurrentPlaceRxLibGateway() {
        locationProvider = new ReactiveLocationProvider(CustomApplication.get());
    }

    @Override public Observable<List<Place>> get() {
        return locationProvider.getCurrentPlace(null)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::checkStatusSuccess)
                .flatMap(this::convert);
    }

    private void checkStatusSuccess(PlaceLikelihoodBuffer buffer) {
        if (!buffer.getStatus().isSuccess()) {
            buffer.release();
            throw new UnSuccessPredictionException();
        }
    }

    private Observable<List<Place>> convert(PlaceLikelihoodBuffer placeBuffer) {
       return Observable.from(placeBuffer)
               .map(this::convert)
               .toList()
               .doOnNext(result -> placeBuffer.release());
    }

    private Place convert(PlaceLikelihood place) {
        PlaceLikelihood freeze = place.freeze();
        return freeze.getPlace();
    }


}
