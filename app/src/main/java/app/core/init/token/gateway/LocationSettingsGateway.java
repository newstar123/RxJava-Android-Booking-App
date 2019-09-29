package app.core.init.token.gateway;


import rx.Observable;

public interface LocationSettingsGateway {
    Observable<Boolean> get();
}
