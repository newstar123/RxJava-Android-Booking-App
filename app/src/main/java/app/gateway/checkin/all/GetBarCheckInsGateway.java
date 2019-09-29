package app.gateway.checkin.all;

import java.util.List;

import app.core.checkin.friends.entity.CheckinsFriendModel;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GetBarCheckInsGateway {
    @GET("v2/vendors/{id}/checkins") Observable<List<CheckinsFriendModel>> get(@Header("Authorization") String token,
                                                                                    @Path("id") long barId,
                                                                                    @Query("active") String active);
}
