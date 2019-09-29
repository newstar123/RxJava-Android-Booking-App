package app.core.coach.checkin.put.interactor;

import app.core.BaseOutputInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutFirstCheckInCoachStateInteractor implements BaseOutputInteractor<Observable<Boolean>> {

    public PutFirstCheckInCoachStateInteractor(BaseActivity activity) {
    }

    @Override public Observable<Boolean> process() {
        return Observable.just(true)
                .map(val -> QorumSharedCache.checkCheckInCoachMark().save(BaseCacheType.BOOLEAN, val));
    }
}
