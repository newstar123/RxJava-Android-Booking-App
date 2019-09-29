package app.core.uber.tracking.end.gateway;


import rx.Observable;

public interface GetUberRideIdGateway {
    Observable<String> get();
}
