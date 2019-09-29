package app.core.login.facebook.gateway;


import android.os.Bundle;

import app.core.login.facebook.entity.LoginResponse;
import rx.Observable;

public interface PutLocalAccountGateway {
    Observable<Bundle> put(LoginResponse response);
}
