package app.core.coachmark.tab.get;


import app.core.BaseOutputInteractor;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetTabCoachMarkInteractor implements BaseOutputInteractor<Observable<Boolean>> {

    public GetTabCoachMarkInteractor() { }

    @Override
    public Observable<Boolean> process() {
        return Observable.just(QorumSharedCache.checkTabCoachMark().get(BaseCacheType.BOOLEAN));
    }
}
