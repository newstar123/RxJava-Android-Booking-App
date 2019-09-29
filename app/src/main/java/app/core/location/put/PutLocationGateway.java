package app.core.location.put;

import android.location.Location;

import rx.Observable;

public interface PutLocationGateway {
    Observable<Location> put(Location userId);
}
