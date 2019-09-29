package app.core.profile.put.gateway;

import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import rx.Observable;

public interface PutProfileGateway {
    Observable<LoginResponse> put(long userId, PutProfileModel model);
}
