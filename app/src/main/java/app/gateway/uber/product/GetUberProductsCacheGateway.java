package app.gateway.uber.product;


import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.List;

import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.product.entity.UberProductResponse;
import app.core.uber.product.entity.UberProductsResponse;
import app.core.uber.product.gateway.GetUberProductsGateway;
import app.gateway.uber.estimate.cache.RuntimeUberRideEstimatesCache;
import rx.Observable;

public class GetUberProductsCacheGateway implements GetUberProductsGateway {


    public GetUberProductsCacheGateway() {
    }

    @Override public Observable<UberProductsResponse> get(LatLng start) {
        if (RuntimeUberRideEstimatesCache.rideEstimatesResponse == null)
            return Observable.just(null);
        List<UberProductResponse> uberProductResponses = Observable.from(RuntimeUberRideEstimatesCache.rideEstimatesResponse)
                .filter(estimate -> isCoordinatesEqual(estimate, start))
                .filter(this::isNotExpired)
                .map(PostUberEstimateResponse::getProduct)
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        if (uberProductResponses.isEmpty())
            return Observable.just(null);
        else
            return Observable.just(new UberProductsResponse(uberProductResponses));
    }

    private boolean isCoordinatesEqual(PostUberEstimateResponse estimateInCache, LatLng departure) {
        boolean b = estimateInCache.getDeparture().equals(departure);
        return b;
    }


    private boolean isNotExpired(PostUberEstimateResponse estimateInCache) {
        long l = System.currentTimeMillis();
        long timestamp = estimateInCache.getTimestamp();
        boolean b = l - timestamp < 60000;
        return b;
    }


}
