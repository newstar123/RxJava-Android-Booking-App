package app.core.location.route.gateway;


import com.google.android.gms.location.places.Place;

import rx.Observable;

public interface LocationByPlaceIdGateway {
    Observable<Place> get(String placeId);
}
