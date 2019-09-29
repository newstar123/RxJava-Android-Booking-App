package app.gateway.location.blocker;


import app.core.location.blocker.entity.LocationsDemandRadiusResponse;
import retrofit2.http.GET;
import rx.Observable;

public interface DemandRadiusGateway {
    @GET("v2/locations/demand/radius") Observable<LocationsDemandRadiusResponse> get();
}
