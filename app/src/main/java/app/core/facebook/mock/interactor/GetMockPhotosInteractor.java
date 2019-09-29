package app.core.facebook.mock.interactor;

import app.core.BaseOutputInteractor;
import app.core.facebook.mock.entity.MockPhotosResponse;
import app.delivering.component.BaseActivity;
import app.gateway.facebook.mock.GetMockPhotosRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetMockPhotosInteractor implements BaseOutputInteractor<Observable<MockPhotosResponse>> {
    private GetMockPhotosRestGateway mockPhotosRestGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetMockPhotosInteractor(BaseActivity activity){
        mockPhotosRestGateway = new GetMockPhotosRestGateway();
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<MockPhotosResponse> process() {
        return networkPermissionGateway.check()
                .concatMap(isOk -> mockPhotosRestGateway.get());
    }
}
