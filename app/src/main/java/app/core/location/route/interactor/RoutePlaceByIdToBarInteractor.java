package app.core.location.route.interactor;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import app.core.BaseInteractor;
import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import app.core.location.route.entity.RoutePlaceByIdToBarResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyRoutesException;
import app.gateway.direction.GoogleRouteGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class RoutePlaceByIdToBarInteractor implements BaseInteractor<RoutePlaceByIdToBarRequest,
        Observable<RoutePlaceByIdToBarResponse>> {
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private final GoogleRouteGateway routeGateway;
    private final List<LatLng> newDecodedPath;

    public RoutePlaceByIdToBarInteractor(BaseActivity activity) {
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        routeGateway = new GoogleRouteGateway(activity);
        newDecodedPath = new ArrayList<>();
    }

    @Override public Observable<RoutePlaceByIdToBarResponse> process(RoutePlaceByIdToBarRequest request) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> routeGateway.get(request))
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::convert);
    }

    private RoutePlaceByIdToBarResponse convert(DirectionsResult directionsResult) {
        if (!newDecodedPath.isEmpty())
            newDecodedPath.clear();
        for (DirectionsRoute route : directionsResult.routes) {
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
            // All the LatLng coordinates of one polyline.
            for(com.google.maps.model.LatLng latLng: decodedPath) {
                newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
            }
        }
        if (newDecodedPath.isEmpty())
            throw new EmptyRoutesException();
        return new RoutePlaceByIdToBarResponse(newDecodedPath);
    }
}
