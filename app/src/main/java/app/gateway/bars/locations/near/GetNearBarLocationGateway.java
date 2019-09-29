package app.gateway.bars.locations.near;

import android.location.Location;

import java.util.Collections;
import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.core.location.service.entity.DistanceCalculator;
import rx.Observable;

public class GetNearBarLocationGateway implements GetNearLocationGateway {

    @Override public Observable<LocationsModel> sort(List<LocationsModel> locations, Location location) {
        return Observable.just(locations)
                .map(locationModels -> getNearest(locationModels, location));
    }

    private LocationsModel getNearest(List<LocationsModel> serviceLocations, Location location) {
        serviceLocations = Collections.synchronizedList(serviceLocations);
        LocationsModel nearestLocation = serviceLocations.get(0);
        double minimalDistance = compute(nearestLocation, location);
        for (LocationsModel model : serviceLocations) {
            double distanceToLocation = compute(model, location);
            if (minimalDistance > distanceToLocation){
                minimalDistance = distanceToLocation;
                nearestLocation = model;
            }
        }
        return nearestLocation;
    }

    private float compute(LocationsModel o1, Location location) {
        return DistanceCalculator.computeDistanceAndBearing(o1.getLatitude(), o1.getLongitude(), location.getLatitude(), location.getLongitude());
    }
}
