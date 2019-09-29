package app.core.uber.cache.get.interactor;


import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.uber.cache.get.gateway.GetUberRideEstimateCacheGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.gateway.uber.estimate.cache.get.GetUberRideEstimateRuntimeCacheGateway;
import rx.Observable;

public class GetUberRideEstimatesCacheInteractor implements BaseOutputInteractor<Observable<List<PostUberEstimateResponse>>> {
    private final GetUberRideEstimateCacheGateway getUberRideEstimateCacheGateway;

    public GetUberRideEstimatesCacheInteractor() {
        getUberRideEstimateCacheGateway = new GetUberRideEstimateRuntimeCacheGateway();
    }

    @Override public Observable<List<PostUberEstimateResponse>> process() {
        return getUberRideEstimateCacheGateway.get();
    }
}
