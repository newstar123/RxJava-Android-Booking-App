package app.gateway.analytics;

import rx.Observable;

public interface FirebaseLogGateway {
    Observable<Boolean> log();
}
