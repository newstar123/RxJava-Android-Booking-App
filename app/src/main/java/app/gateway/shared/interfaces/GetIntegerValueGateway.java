package app.gateway.shared.interfaces;

import rx.Observable;

public interface GetIntegerValueGateway {
    Observable<Integer> get();
}
