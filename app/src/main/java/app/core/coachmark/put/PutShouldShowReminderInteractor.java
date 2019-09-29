package app.core.coachmark.put;

import app.core.BaseInteractor;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutShouldShowReminderInteractor implements BaseInteractor<Integer, Observable<Integer>> {


    public PutShouldShowReminderInteractor() {
    }

    @Override public Observable<Integer> process(Integer value) {
        return Observable.just(value)
                .map(val -> QorumSharedCache.checkReminder().save(BaseCacheType.INT, value))
                .concatMap(val -> Observable.just(value));
    }

}
