package app.gateway.ride.register;

import app.core.uber.start.entity.RegisterRideRequest;
import app.core.uber.start.entity.RegisterRideResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;


public interface RegisterRideRetrofitGateway {
    @POST("v2/patrons/{id}/rides/uber") Observable<RegisterRideResponse> post(@Header("Authorization") String token,
                                                                              @Path("id") long userId,
                                                                              @Body RegisterRideRequest model);

}
