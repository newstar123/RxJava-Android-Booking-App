package app.core.uber.delete.activity.interactor;


import app.core.BaseInteractor;
import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.activity.gateway.DeleteUberGateway;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.uber.delete.DeleteUberSdkGateway;
import rx.Observable;

public class DeleteUberInteractor implements BaseInteractor<DeleteUberRequest
        , Observable<DeleteUberResponse>>{
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private final DeleteUberGateway deleteUberSdkGateway;

    public DeleteUberInteractor(BaseActivity activity) {
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        deleteUberSdkGateway = new DeleteUberSdkGateway(activity);
    }

    @Override public Observable<DeleteUberResponse> process(DeleteUberRequest deleteUberRequest) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> deleteUberSdkGateway.delete(deleteUberRequest));
    }
}
