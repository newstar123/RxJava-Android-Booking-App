package app.gateway.uber.estimate.cache.get;


import java.util.List;

import app.core.uber.cache.get.gateway.GetUberRideEstimateCacheGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.gateway.uber.estimate.cache.RuntimeUberRideEstimatesCache;
import rx.Observable;

public class GetUberRideEstimateRuntimeCacheGateway implements GetUberRideEstimateCacheGateway {
    @Override public Observable<List<PostUberEstimateResponse>> get() {
        return Observable.just(RuntimeUberRideEstimatesCache.rideEstimatesResponse);
    }
}
