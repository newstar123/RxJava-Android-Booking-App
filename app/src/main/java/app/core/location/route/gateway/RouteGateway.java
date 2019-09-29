package app.core.location.route.gateway;


import com.google.maps.model.DirectionsResult;

import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import rx.Observable;

public interface RouteGateway {
    Observable<DirectionsResult> get(RoutePlaceByIdToBarRequest request);
}
