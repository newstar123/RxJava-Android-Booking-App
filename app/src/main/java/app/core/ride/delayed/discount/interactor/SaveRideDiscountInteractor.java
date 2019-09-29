package app.core.ride.delayed.discount.interactor;

import app.core.BaseInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class SaveRideDiscountInteractor implements BaseInteractor<Float, Observable<EmptyResponse>> {

    public SaveRideDiscountInteractor() { }

    @Override
    public Observable<EmptyResponse> process(Float value) {
        return Observable.just(value)
                .map(val -> QorumSharedCache.checkSavedFreeRideValue().save(BaseCacheType.FLOAT, value))
                .map(res -> new EmptyResponse());
    }
}
