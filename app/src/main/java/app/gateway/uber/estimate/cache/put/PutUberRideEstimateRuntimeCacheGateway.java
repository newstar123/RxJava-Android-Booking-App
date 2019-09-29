package app.gateway.uber.estimate.cache.put;

import java.util.List;

import app.core.uber.cache.put.gateway.PutUberRideEstimateCacheGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.gateway.uber.estimate.cache.RuntimeUberRideEstimatesCache;
import rx.Observable;


public class PutUberRideEstimateRuntimeCacheGateway implements PutUberRideEstimateCacheGateway {

    @Override public Observable<List<PostUberEstimateResponse>> put(List<PostUberEstimateResponse> estimates) {
        RuntimeUberRideEstimatesCache.rideEstimatesResponse = estimates;
        return Observable.just(estimates);
    }
}
