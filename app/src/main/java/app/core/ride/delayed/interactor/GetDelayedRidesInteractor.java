package app.core.ride.delayed.interactor;

import android.content.Context;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.ride.delayed.discount.interactor.SaveRideDiscountInteractor;
import app.core.ride.delayed.entity.DelayedRidesResponse;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.ride.delayed.GetDelayedRidesRestGateway;
import rx.Observable;

public class GetDelayedRidesInteractor implements BaseOutputInteractor<Observable<DelayedRidesResponse>> {
    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetDelayedRidesRestGateway ridesRestGateway;
    private final SaveRideDiscountInteractor saveRideDiscountInteractor;

    public GetDelayedRidesInteractor(Context context) {
        this.ridesRestGateway = new GetDelayedRidesRestGateway(context);
        this.networkPermissionGateway = new CheckNetworkPermissionGateway(context);
        this.saveRideDiscountInteractor = new SaveRideDiscountInteractor();
    }

    @Override public Observable<DelayedRidesResponse> process() {
        return networkPermissionGateway.check()
                .concatMap(isOk -> ridesRestGateway.get())
                .flatMap(list -> {
                    if (list == null || list.isEmpty())
                        return resetCache(list);
                    else
                        return saveToCache(new DelayedRidesResponse(list));
                });
    }

    private Observable<DelayedRidesResponse> resetCache(final List<GetCheckInsResponse> list) {
        return saveRideDiscountInteractor.process(0f)
                .doOnNext(val -> QorumSharedCache.checkLastFreeRideCheckInId().save(BaseCacheType.LONG, 0L))
                .map(val -> new DelayedRidesResponse(list));
    }

    private Observable<DelayedRidesResponse> saveToCache(DelayedRidesResponse delayedRidesResponse) {
        return Observable.just(QorumSharedCache.checkLastFreeRideCheckInId().save(BaseCacheType.LONG,
                getFirstCheckInId(delayedRidesResponse.getTabsWithFreeRides())))
                .concatMap(val -> saveRideDiscountInteractor.process(getFirstFreeRideVal(delayedRidesResponse.getTabsWithFreeRides())))
                .map(val -> delayedRidesResponse);
    }

    private long getFirstCheckInId(List<GetCheckInsResponse> response) {
        return response.get(0).getId();
    }

    private float getFirstFreeRideVal(List<GetCheckInsResponse> response) {
        return (float) response.get(0).getRideDiscount().getDiscountValue();
    }
}
