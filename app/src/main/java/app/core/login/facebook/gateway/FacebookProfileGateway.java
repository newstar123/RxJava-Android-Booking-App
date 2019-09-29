package app.core.login.facebook.gateway;


import app.core.login.facebook.entity.FacebookProfileResponse;
import rx.Observable;

public interface FacebookProfileGateway {
    Observable<FacebookProfileResponse> get();
}
