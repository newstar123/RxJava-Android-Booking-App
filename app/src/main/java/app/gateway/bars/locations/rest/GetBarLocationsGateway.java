package app.gateway.bars.locations.rest;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import retrofit2.http.GET;
import rx.Observable;

public interface GetBarLocationsGateway {
    @GET("v2/locations") Observable<List<LocationsModel>> get();
}
