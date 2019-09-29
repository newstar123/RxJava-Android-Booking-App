package app.core.logout.gateway;


import rx.Observable;

public interface LogoutGateway {
    Observable<Object> logout();
}
