package app.delivering.mvp.bars.list.freeride.presenter;

import app.core.ride.delayed.entity.DelayedRidesResponse;
import app.core.ride.delayed.interactor.GetDelayedRidesInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetFreeRidesPresenter extends BaseOutputPresenter<Observable<DelayedRidesResponse>> {
    private final GetDelayedRidesInteractor freeRidesInteractor;
    private DelayedRidesResponse delayedRidesResponse;

    public GetFreeRidesPresenter(BaseActivity activity) {
        super(activity);
        freeRidesInteractor = new GetDelayedRidesInteractor(activity);
    }

    @Override
    public Observable<DelayedRidesResponse> process() {
        return freeRidesInteractor.process()
                .doOnNext(response -> this.delayedRidesResponse = response)
                .doOnNext(freeRidesValue -> freeRidesValue.setFreeRideMarkAlreadyShown(
                        QorumSharedCache.checkUberFreeRideMark().get(BaseCacheType.BOOLEAN)))
                .map(next -> this.delayedRidesResponse);
    }
}
