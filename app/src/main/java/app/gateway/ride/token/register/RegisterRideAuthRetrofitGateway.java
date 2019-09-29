package app.gateway.ride.token.register;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;


public interface RegisterRideAuthRetrofitGateway {
    @POST("/api/v2/patrons/{userId}/ride_token/uber")
    Observable<RegisterRideAuthResponse> post(@Header("Authorization") String token,
                                           @Path("userId") long userId,
                                           @Body RegisterRideAuthRequest request);
}
