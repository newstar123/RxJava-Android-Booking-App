package app.core.checkin.bar.gateway;

import rx.Observable;

public interface PutCheckedInBarId {
    Observable<Long> put(long userId);
}
