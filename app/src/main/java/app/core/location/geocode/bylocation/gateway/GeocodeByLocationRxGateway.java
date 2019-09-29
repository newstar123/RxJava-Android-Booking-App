package app.core.location.geocode.bylocation.gateway;


import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import rx.Observable;

public interface GeocodeByLocationRxGateway {
    Observable<Address> get(LatLng latLng);
}
