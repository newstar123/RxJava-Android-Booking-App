package app.gateway.location.blocker;


import app.core.location.blocker.entity.LocationDemandRequest;
import app.core.location.blocker.entity.LocationsDemandResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface LocationsDemandGateway {
    @POST("v2/locations/demand") Observable<LocationsDemandResponse> post(
            @Body LocationDemandRequest locationDemandRequest);
}
