package app.core.uber.cache.put.gateway;


import java.util.List;

import app.core.uber.fares.entity.PostUberEstimateResponse;
import rx.Observable;

public interface PutUberRideEstimateCacheGateway {
    Observable<List<PostUberEstimateResponse>> put(List<PostUberEstimateResponse> estimates);
}
