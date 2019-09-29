package app.gateway.shared.interfaces;

import rx.Observable;

public interface PutStringValueGateway {
    Observable<String> put(String value);
}
