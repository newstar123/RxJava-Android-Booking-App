package app.gateway.shared.interfaces;

import rx.Observable;

public interface PutBooleanValueGateway {
    Observable<Boolean> put(boolean value);
}
