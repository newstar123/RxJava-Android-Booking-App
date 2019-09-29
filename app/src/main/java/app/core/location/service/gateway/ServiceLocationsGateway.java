package app.core.location.service.gateway;


import java.util.List;

import app.core.location.service.entity.ServiceLocation;
import retrofit2.http.GET;
import rx.Observable;

public interface ServiceLocationsGateway {
    @GET("v2/locations")  Observable<List<ServiceLocation>> get();
}
