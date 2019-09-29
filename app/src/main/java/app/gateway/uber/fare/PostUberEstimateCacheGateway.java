package app.gateway.uber.fare;

import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.gateway.uber.estimate.cache.RuntimeUberRideEstimatesCache;
import rx.Observable;


public class PostUberEstimateCacheGateway implements PostUberEstimateGateway {
    @Override public Observable<PostUberEstimateResponse> post(PostUberEstimateRequest request) {
        return Observable.from(RuntimeUberRideEstimatesCache.rideEstimatesResponse)
                .filter(PostUberEstimateCacheGateway::isNotExpired)
                .filter(cache -> isProductEqual(cache, request))
                .filter(cache -> isCoordinatesEqual(cache, request))
                .firstOrDefault(null);
    }

    private boolean isCoordinatesEqual(PostUberEstimateResponse estimateInCache, PostUberEstimateRequest request) {
        boolean b = estimateInCache.getDeparture().equals(request.getDeparture()) && estimateInCache.getDestination().equals(request.getDestination());
        return b;
    }

    private boolean isProductEqual(PostUberEstimateResponse estimateInCache, PostUberEstimateRequest request) {
        boolean equals = request.getProduct().getProductId().equals(estimateInCache.getProduct().getProductId());
        return equals;
    }

    public static boolean isNotExpired(PostUberEstimateResponse estimateInCache) {
        boolean b = System.currentTimeMillis() - estimateInCache.getTimestamp() < 60000;
        return b;
    }
}
