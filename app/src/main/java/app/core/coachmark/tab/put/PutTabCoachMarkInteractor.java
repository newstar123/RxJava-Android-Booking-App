package app.core.coachmark.tab.put;

import app.core.BaseInteractor;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutTabCoachMarkInteractor implements BaseInteractor<Boolean, Observable<Boolean>> {

    public PutTabCoachMarkInteractor() { }

    @Override
    public Observable<Boolean> process(Boolean value) {
        return Observable.just(value)
                .map(val -> QorumSharedCache.checkTabCoachMark().save(BaseCacheType.BOOLEAN, value));
    }
}
