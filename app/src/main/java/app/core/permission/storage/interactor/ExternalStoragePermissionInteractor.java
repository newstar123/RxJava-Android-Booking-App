package app.core.permission.storage.interactor;

import app.core.BaseOutputInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.storage.CheckExternalStoragePermissionGateway;
import rx.Observable;

public class ExternalStoragePermissionInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private CheckExternalStoragePermissionGateway permissionGateway;

    public ExternalStoragePermissionInteractor(BaseActivity activity) {
        permissionGateway = new CheckExternalStoragePermissionGateway(activity);
    }

    @Override public Observable<Boolean> process() {
        return  permissionGateway.check();
    }

}
