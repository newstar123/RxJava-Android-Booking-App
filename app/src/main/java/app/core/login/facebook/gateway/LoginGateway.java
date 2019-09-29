package app.core.login.facebook.gateway;


import app.core.login.facebook.entity.LoginRequest;
import app.core.login.facebook.entity.LoginResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface LoginGateway {
    @POST("v2/patron") Observable<LoginResponse> post(@Body LoginRequest loginRequest);
}
