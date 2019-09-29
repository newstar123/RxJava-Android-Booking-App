package app.core.ride.delayed.discount.interactor;

import app.core.BaseOutputInteractor;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetRideDiscountInteractor implements BaseOutputInteractor<Observable<Float>> {

    public GetRideDiscountInteractor() {

    }

    @Override
    public Observable<Float> process() {
        return Observable.just(true)
                .map(res -> QorumSharedCache.checkSavedFreeRideValue().get(BaseCacheType.FLOAT));
    }
}
