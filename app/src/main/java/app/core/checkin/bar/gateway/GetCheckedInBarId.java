package app.core.checkin.bar.gateway;

import rx.Observable;

public interface GetCheckedInBarId {
    Observable<Long> get();
}
