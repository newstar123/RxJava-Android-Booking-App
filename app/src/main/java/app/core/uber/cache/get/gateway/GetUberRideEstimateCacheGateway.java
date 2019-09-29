package app.core.uber.cache.get.gateway;


import java.util.List;

import app.core.uber.fares.entity.PostUberEstimateResponse;
import rx.Observable;

public interface GetUberRideEstimateCacheGateway {
    Observable<List<PostUberEstimateResponse>> get();
}
