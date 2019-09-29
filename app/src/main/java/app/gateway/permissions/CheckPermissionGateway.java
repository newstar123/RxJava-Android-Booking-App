package app.gateway.permissions;

import rx.Observable;

public interface CheckPermissionGateway {
    Observable<Boolean> check();
}
