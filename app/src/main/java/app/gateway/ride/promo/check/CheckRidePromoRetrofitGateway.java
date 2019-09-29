package app.gateway.ride.promo.check;

import app.core.uber.start.entity.CheckRidePromoResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;


public interface CheckRidePromoRetrofitGateway {
    @GET("/api/v2/patrons/{userId}/checkins/{checkinId}/uber/ride_promo")
    Observable<CheckRidePromoResponse> get(@Header("Authorization") String token,
                                           @Path("userId") long userId,
                                           @Path("checkinId") long checkinId);
}
