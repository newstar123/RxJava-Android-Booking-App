package app.core.uber.fares.interactor;

import app.core.BaseInteractor;
import app.core.uber.fares.entity.PostUberEstimatesRequest;
import app.core.uber.fares.entity.PostUberEstimatesResponse;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.uber.estimate.cache.put.PutUberRideEstimateRuntimeCacheGateway;
import app.gateway.uber.fares.PostUberEstimatesMainGateway;
import rx.Observable;


public class UberFaresInteractor implements BaseInteractor<PostUberEstimatesRequest, Observable<PostUberEstimatesResponse>> {
    private final PostUberEstimatesMainGateway postUberEstimatesGateway;
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private final PutUberRideEstimateRuntimeCacheGateway putUberRideEstimateCacheGateway;

    public UberFaresInteractor(BaseActivity activity) {
        postUberEstimatesGateway = new PostUberEstimatesMainGateway(activity);
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        putUberRideEstimateCacheGateway = new PutUberRideEstimateRuntimeCacheGateway();
    }

    @Override public Observable<PostUberEstimatesResponse> process(PostUberEstimatesRequest postUberEstimatesRequest) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> postUberEstimatesGateway.post(postUberEstimatesRequest))
                .concatMap(this::cacheResult);
    }

    private Observable<PostUberEstimatesResponse> cacheResult(PostUberEstimatesResponse response) {
        return putUberRideEstimateCacheGateway.put(response.getEstimates())
                .concatMap(result -> Observable.just(response));
    }
}
