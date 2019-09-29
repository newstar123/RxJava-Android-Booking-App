package app.core.permission.camera.interactor;

import app.core.BaseOutputInteractor;
import app.core.permission.storage.interactor.ExternalStoragePermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.camera.CheckCameraPermissionGateway;
import rx.Observable;

public class CameraPermissionInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private CheckCameraPermissionGateway permissionGateway;
    private ExternalStoragePermissionInteractor externalStoragePermissionInteractor;

    public CameraPermissionInteractor(BaseActivity activity) {
        permissionGateway = new CheckCameraPermissionGateway(activity);
        externalStoragePermissionInteractor = new ExternalStoragePermissionInteractor(activity);
    }

    @Override public Observable<Boolean> process() {
        return  permissionGateway.check()
                .concatMap(isOk -> externalStoragePermissionInteractor.process());
    }

}
