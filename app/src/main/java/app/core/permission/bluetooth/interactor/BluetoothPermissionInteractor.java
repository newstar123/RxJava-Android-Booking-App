package app.core.permission.bluetooth.interactor;

import app.core.BaseOutputInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.bluetooth.CheckBluetoothPermissionGateway;
import rx.Observable;

public class BluetoothPermissionInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private CheckBluetoothPermissionGateway permissionGateway;

    public BluetoothPermissionInteractor(BaseActivity activity) {
        permissionGateway = new CheckBluetoothPermissionGateway(activity);
    }

    @Override public Observable<Boolean> process() {
        return permissionGateway.check();
    }

}
