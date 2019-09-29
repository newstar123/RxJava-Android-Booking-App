package app.gateway.checkin.item;

import app.core.checkin.user.post.entity.CheckInResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GetCheckInByIdGateway {
    @GET("v2/patrons/{id}/checkins/{cid}") Observable<CheckInResponse> get(@Header("Authorization") String token,
                                                                           @Path("id") long userId,
                                                                           @Path("cid") long checkInId,
                                                                           @Query("force") boolean isForceRequest);
}
