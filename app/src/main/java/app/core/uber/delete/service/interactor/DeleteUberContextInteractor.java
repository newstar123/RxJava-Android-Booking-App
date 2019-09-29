package app.core.uber.delete.service.interactor;


import app.core.BaseInteractor;
import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import app.core.uber.delete.activity.gateway.DeleteUberGateway;
import app.gateway.uber.delete.DeleteUberSdkContextGateway;
import rx.Observable;

public class DeleteUberContextInteractor implements BaseInteractor<DeleteUberRequest
        , Observable<DeleteUberResponse>> {
    private final DeleteUberGateway deleteUberSdkGateway;

    public DeleteUberContextInteractor() {
        deleteUberSdkGateway = new DeleteUberSdkContextGateway();
    }

    @Override public Observable<DeleteUberResponse> process(DeleteUberRequest deleteUberRequest) {
        return deleteUberSdkGateway.delete(deleteUberRequest);
    }


}
