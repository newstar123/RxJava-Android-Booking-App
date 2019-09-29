package app.core.profile.photo.post.interactor;

import app.core.BaseInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.photo.post.entity.PutProfilePhotoModel;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.profile.photo.put.PutProfilePhotoRestGateway;
import rx.Observable;

public class PutProfilePhotoInteractor implements BaseInteractor<PutProfilePhotoModel, Observable<EmptyResponse>> {
    private CheckNetworkPermissionGateway networkPermissionGateway;
    private PutProfilePhotoRestGateway putProfilePhotoRestGateway;

    public PutProfilePhotoInteractor(BaseActivity activity) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        putProfilePhotoRestGateway = new PutProfilePhotoRestGateway(activity);
    }

    @Override public Observable<EmptyResponse> process(PutProfilePhotoModel putProfilePhotoModel) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> putProfilePhotoRestGateway.put(putProfilePhotoModel));
    }
}
