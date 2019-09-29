package app.gateway.version;

import rx.Observable;

public interface GetAvailableAppVersion {
    Observable<String> getAppVersion();
}
