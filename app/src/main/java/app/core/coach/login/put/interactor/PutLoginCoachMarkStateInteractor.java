package app.core.coach.login.put.interactor;

import app.core.BaseOutputInteractor;
import app.core.coach.entity.BooleanResponse;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutLoginCoachMarkStateInteractor implements BaseOutputInteractor<Observable<BooleanResponse>> {

    public PutLoginCoachMarkStateInteractor() {
    }

    @Override public Observable<BooleanResponse> process() {
        return Observable.just((boolean)QorumSharedCache.checkLoginCoachMark().get(BaseCacheType.BOOLEAN))
                .map(BooleanResponse::new);
    }
}
