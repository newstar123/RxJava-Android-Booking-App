package app.gateway.shared.interfaces;

import rx.Observable;

public interface PutIntegerValueGateway {
    Observable<Integer> put(int value);
}
