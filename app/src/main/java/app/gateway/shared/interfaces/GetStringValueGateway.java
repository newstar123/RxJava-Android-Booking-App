package app.gateway.shared.interfaces;

import rx.Observable;

public interface GetStringValueGateway {
    Observable<String> get();
}
