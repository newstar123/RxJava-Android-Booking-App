package app.core.uber.auth.gateway;


import app.core.uber.auth.entity.UberAuthToken;
import rx.Observable;

public interface GetUberAuthTokenGateway {
    Observable<UberAuthToken> get();
}
