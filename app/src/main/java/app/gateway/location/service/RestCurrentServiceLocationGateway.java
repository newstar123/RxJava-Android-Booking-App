package app.gateway.location.service;

import android.location.Location;

import java.util.Collections;
import java.util.List;

import app.core.location.service.entity.DistanceCalculator;
import app.core.location.service.entity.ServiceLocation;
import app.core.location.service.gateway.CurrentServiceLocationGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RestCurrentServiceLocationGateway implements CurrentServiceLocationGateway {
    private final RestServiceLocationsGateway restServiceLocationsGateway;

    public RestCurrentServiceLocationGateway() {
        restServiceLocationsGateway = new RestServiceLocationsGateway();
    }

    @Override public Observable<ServiceLocation> get(Location location) {
        return restServiceLocationsGateway.get()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(serviceLocations -> sortByDistance(serviceLocations, location))
                .map(serviceLocations -> serviceLocations.get(0));

    }

    private void sortByDistance(List<ServiceLocation> serviceLocations, Location location) {
        Collections.sort(serviceLocations, (o1, o2) -> compareLocations(o1, o2, location));
    }

    private int compareLocations(ServiceLocation o1, ServiceLocation o2, Location location) {
        float distanceToServiceLocation1 = compute(o1, location);
        float distanceToServiceLocation2 = compute(o2, location);
        return (int) (distanceToServiceLocation1 - distanceToServiceLocation2);
    }

    private float compute(ServiceLocation o1, Location location) {
        return DistanceCalculator.computeDistanceAndBearing(o1.getLatitude(), o1.getLongitude(), location.getLatitude(), location.getLongitude());
    }
}
