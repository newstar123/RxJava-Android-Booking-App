package app.gateway.ride.delayed;

import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface GetDelayedRidesRetrofitGateway {
    @GET("/api/v2/patrons/{patron_id}/checkins/ride_discount/idle")
    Observable<List<GetCheckInsResponse>> get(@Header("Authorization") String token,
                                              @Path("patron_id") long userId);
}
