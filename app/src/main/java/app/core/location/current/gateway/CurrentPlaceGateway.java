package app.core.location.current.gateway;

import com.google.android.gms.location.places.Place;

import java.util.List;

import rx.Observable;

public interface CurrentPlaceGateway {
    Observable<List<Place>> get();
}
