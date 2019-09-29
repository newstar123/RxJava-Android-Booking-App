package app.core.uber.product.interactor;


import java.util.Collections;
import java.util.List;

import app.core.BaseInteractor;
import app.core.uber.cache.put.gateway.PutUberRideEstimateCacheGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.product.entity.UberProductRequest;
import app.core.uber.product.entity.UberProductResponse;
import app.core.uber.product.entity.UberProductsResponse;
import app.core.uber.product.gateway.GetUberProductsGateway;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.uber.estimate.cache.put.PutUberRideEstimateRuntimeCacheGateway;
import app.gateway.uber.product.GetUberProductsMainGateway;
import rx.Observable;

public class UberProductsInteractor implements BaseInteractor<UberProductRequest,
        Observable<UberProductsResponse>> {
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private final GetUberProductsGateway getUberProductsGateway;
    private final PutUberRideEstimateCacheGateway putUberRideEstimateCacheGateway;

    public UberProductsInteractor(BaseActivity activity) {
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        getUberProductsGateway = new GetUberProductsMainGateway(activity);
        putUberRideEstimateCacheGateway = new PutUberRideEstimateRuntimeCacheGateway();
    }

    @Override public Observable<UberProductsResponse> process(UberProductRequest uberProductRequest) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> getUberProductsGateway.get(uberProductRequest.getDeparture()))
                .concatMap(response -> cacheResult(response, uberProductRequest));
    }

    private Observable<UberProductsResponse> cacheResult(UberProductsResponse response, UberProductRequest uberProductRequest) {
        List<PostUberEstimateResponse> postUberEstimateResponses = Observable.from(response.getProducts())
                .map(estimate -> convert(estimate, uberProductRequest))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
       return putUberRideEstimateCacheGateway
               .put(postUberEstimateResponses)
               .concatMap(result -> Observable.just(response));
    }

    private PostUberEstimateResponse convert(UberProductResponse product, UberProductRequest uberProductRequest) {
        PostUberEstimateResponse postUberEstimateResponse = new PostUberEstimateResponse();
        postUberEstimateResponse.setDeparture(uberProductRequest.getDeparture());
        postUberEstimateResponse.setProduct(product);
        return postUberEstimateResponse;
    }
}
