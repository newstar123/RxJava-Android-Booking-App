package app.core.location.get;

import android.location.Location;

import rx.Observable;

public interface GetRxLocationGateway {
     Observable<Location> get();
}
