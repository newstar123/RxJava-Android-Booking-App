package app.delivering.mvp.ride.order.type.select.binder.subbinder;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Collections;
import java.util.List;

import app.core.location.service.entity.DistanceCalculator;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import rx.Observable;

public class ArcRouteOnMapSubBinder {
    private final RouteOnMapSubBinder routeOnMapSubBinder;

    public ArcRouteOnMapSubBinder(BaseActivity activity) {
        routeOnMapSubBinder = new RouteOnMapSubBinder(activity);
    }

    public void apply(MapView mapView, List<LatLng> route){
        MapObservable.get(mapView).subscribe(map -> apply(map, route), err -> {});
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        List<LatLng> arcLatLngs = calculateArcLatLng(route);
        routeOnMapSubBinder.apply(map, arcLatLngs);
    }

    private List<LatLng> calculateArcLatLng(List<LatLng> route) {
        LatLng startLatLng = route.get(0);
        LatLng endLatLng = route.get(route.size() - 1);
        float distance = DistanceCalculator.computeDistanceAndBearing(startLatLng.latitude,
                startLatLng.longitude,
                endLatLng.latitude,
                endLatLng.longitude);
        List<LatLng> fractionLatLngs = Observable.range(0, 50)
                .map(fraction -> getDeviationAndCoordinate(fraction * 2, route))
                .buffer(2, 1)
                .map(coordinates-> getDeviatedCoordinates(coordinates, distance))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        fractionLatLngs.add(endLatLng);
        return fractionLatLngs;
    }

    private ArcCoordinateModel getDeviationAndCoordinate(double fraction, List<LatLng> route) {
        ArcCoordinateModel arcCoordinateModel = new ArcCoordinateModel();
        arcCoordinateModel.setFraction(fraction);
        LatLng startLatLng = route.get(0);
        LatLng endLatLng = route.get(route.size() - 1);
        LatLng fractionLatLng = SphericalUtil.interpolate(startLatLng, endLatLng, fraction / 100d);
        arcCoordinateModel.setLatLng(fractionLatLng);
        double fractionInRadian = (Math.PI / 100d) * fraction;
        double sin = Math.sin(fractionInRadian);
        arcCoordinateModel.setDeviation(sin);
        return arcCoordinateModel;
    }

    private LatLng getDeviatedCoordinates(List<ArcCoordinateModel> coordinates, float distance) {
        if (coordinates.size() < 2)
            return coordinates.get(0).getLatLng();
        LatLng currentLatLng = coordinates.get(0).getLatLng();
        LatLng nextLatLng = coordinates.get(1).getLatLng();
        double heading = SphericalUtil.computeHeading(currentLatLng, nextLatLng);
        double distanceWithDeviation = distance / 5 * coordinates.get(0).getDeviation();
        return SphericalUtil.computeOffset(currentLatLng, distanceWithDeviation, heading + 90);
    }

}
