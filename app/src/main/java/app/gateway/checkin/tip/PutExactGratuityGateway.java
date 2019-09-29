package app.gateway.checkin.tip;

import app.core.checkin.tip.entity.ExactGratuity;
import app.core.checkin.user.post.entity.CheckInResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutExactGratuityGateway {
    @PUT("v2/patrons/{id}/checkins/{cid}/gratuity") Observable<CheckInResponse> put(@Header("Authorization") String token,
                                                                                    @Path("id") long userId,
                                                                                    @Path("cid") long checkInId,
                                                                                    @Body ExactGratuity gratuity);
}
