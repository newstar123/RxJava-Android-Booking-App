package app.gateway.shared.interfaces;

import rx.Observable;

public interface GetBooleanValueGateway {
    Observable<Boolean> get();
}
