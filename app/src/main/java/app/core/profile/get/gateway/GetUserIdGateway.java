package app.core.profile.get.gateway;


import rx.Observable;

public interface GetUserIdGateway {
    Observable<Long> get();
}
