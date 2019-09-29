package app.gateway.facebook.visibility;


import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PutRetrofitFacebookVisibilityGateway {

    @PUT("v2/patrons/{id}/fb_visible")
    Observable<FacebookVisibilityModel> put(@Header("Authorization") String token,
                                  @Path("id") long userId,
                                  @Body FacebookVisibilityModel body);
}
