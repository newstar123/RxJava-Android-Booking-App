package app.core.login.facebook.gateway;


import rx.Observable;

public interface PutUserIdGateway {
    Observable<Long> put(long userId);
}
