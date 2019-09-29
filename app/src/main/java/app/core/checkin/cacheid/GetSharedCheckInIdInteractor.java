package app.core.checkin.cacheid;

import app.core.BaseOutputInteractor;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetSharedCheckInIdInteractor implements BaseOutputInteractor<Observable<Long>> {

    @Override public Observable<Long> process() {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG));
    }
}
