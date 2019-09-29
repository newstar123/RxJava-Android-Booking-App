package app.core.init.token.gateway;

import app.core.init.token.entity.Token;
import rx.Observable;

public interface AuthTokenGateway {
    Observable<Token> get();
}
