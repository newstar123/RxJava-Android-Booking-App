package app.gateway.checkin.recent;

import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GetCheckInRetrofitGateway {
    @GET("v2/patrons/{id}/checkins")
    Observable<List<GetCheckInsResponse>> post(@Header("Authorization") String token,
                                               @Path("id") long userId,
                                               @Query("recent") int recent);
}
