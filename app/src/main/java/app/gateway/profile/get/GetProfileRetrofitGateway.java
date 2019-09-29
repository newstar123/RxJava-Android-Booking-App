package app.gateway.profile.get;

import app.core.profile.get.entity.ProfileModel;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface GetProfileRetrofitGateway {
    @GET("v2/patrons/{id}") Observable<ProfileModel> get(@Header("Authorization") String token,
                                                              @Path("id") long userId);
}
