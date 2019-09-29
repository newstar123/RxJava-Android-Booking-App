package app.core.uber.fare.interactor;


import app.core.BaseInteractor;
import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.uber.fare.PostUberEstimateRestGateway;
import rx.Observable;

public class UberFareInteractor implements BaseInteractor<PostUberEstimateRequest,
        Observable<PostUberEstimateResponse>> {
    private final PostUberEstimateGateway postUberEstimateGateway;
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;

    public UberFareInteractor(BaseActivity activity) {
        postUberEstimateGateway = new PostUberEstimateRestGateway(activity);
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<PostUberEstimateResponse> process(PostUberEstimateRequest postUberEstimatesRequest) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> postUberEstimateGateway.post(postUberEstimatesRequest));
    }
}
