package app.gateway.profile.put;


import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutProfileRetrofitGateway {
    @PUT("v2/patrons/{patron_id}") Observable<LoginResponse> put(@Header("Authorization") String token,
                                                               @Path("patron_id") long userId,
                                                               @Body PutProfileModel request);
}
