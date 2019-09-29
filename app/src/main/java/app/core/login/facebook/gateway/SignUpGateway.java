package app.core.login.facebook.gateway;


import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.entity.SignUpRequest;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface SignUpGateway {
    @POST("v2/patrons") Observable<LoginResponse> post(@Body SignUpRequest request);

}
