package app.core.permission.gateway;


import rx.Observable;

public interface NetworkAccessGateway {
    Observable<Boolean> check();
}
