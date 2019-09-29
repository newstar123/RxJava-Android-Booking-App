package app.gateway.location.place.bylocation;


import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import app.CustomApplication;
import app.core.location.geocode.bylocation.gateway.GeocodeByLocationRxGateway;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GeocodeByLocationRxLibGateway implements GeocodeByLocationRxGateway {
    private ReactiveLocationProvider locationProvider;

    public GeocodeByLocationRxLibGateway() {
        locationProvider = new ReactiveLocationProvider(CustomApplication.get());
    }

    @Override public Observable<Address> get(LatLng location) {
        return locationProvider
                .getReverseGeocodeObservable(location.latitude, location.longitude, 1)
                .subscribeOn(Schedulers.io())
                .map(addresses -> addresses.get(0));
    }
}
