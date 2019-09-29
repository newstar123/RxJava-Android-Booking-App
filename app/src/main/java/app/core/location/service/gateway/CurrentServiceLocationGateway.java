package app.core.location.service.gateway;


import android.location.Location;

import app.core.location.service.entity.ServiceLocation;
import rx.Observable;

public interface CurrentServiceLocationGateway {
    Observable<ServiceLocation> get(Location location);
}
