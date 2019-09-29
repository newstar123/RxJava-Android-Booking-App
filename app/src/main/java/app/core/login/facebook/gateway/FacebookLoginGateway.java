package app.core.login.facebook.gateway;


import com.facebook.AccessToken;

import rx.Observable;

public interface FacebookLoginGateway {
    Observable<AccessToken> get();
}
