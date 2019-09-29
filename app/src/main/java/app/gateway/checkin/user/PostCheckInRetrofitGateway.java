package app.gateway.checkin.user;

import app.core.checkin.user.post.entity.CheckInRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface PostCheckInRetrofitGateway {
    @POST("v2/patrons/{id}/checkins") Observable<CheckInResponse> post(@Header("Authorization") String token,
                                                                       @Body CheckInRequest request,
                                                                       @Path("id") long userId);
}
