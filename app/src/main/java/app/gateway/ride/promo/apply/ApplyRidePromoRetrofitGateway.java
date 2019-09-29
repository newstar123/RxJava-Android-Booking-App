package app.gateway.ride.promo.apply;

import app.core.payment.regular.model.EmptyResponse;
import app.core.uber.start.entity.ApplyRidePromoResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;


public interface ApplyRidePromoRetrofitGateway {
    @PUT("/api/v2/patrons/{user_id}/checkins/{checkin_id}/uber/{ride_id}/ride_promo")
    Observable<ApplyRidePromoResponse> put(@Header("Authorization") String token,
                                           @Path("user_id") long userId,
                                           @Path("checkin_id") long checkId,
                                           @Path("ride_id") long rideId,
                                           @Body EmptyResponse request);
}
