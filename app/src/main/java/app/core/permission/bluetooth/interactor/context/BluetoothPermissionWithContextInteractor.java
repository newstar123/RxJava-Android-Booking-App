package app.core.permission.bluetooth.interactor.context;

import android.content.Context;

import app.core.BaseOutputInteractor;
import app.gateway.permissions.bluetooth.context.CheckBluetoothContextPermissionGateway;
import rx.Observable;

public class BluetoothPermissionWithContextInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private CheckBluetoothContextPermissionGateway permissionGateway;

    public BluetoothPermissionWithContextInteractor(Context context) {
        permissionGateway = new CheckBluetoothContextPermissionGateway(context);
    }

    @Override public Observable<Boolean> process() {
        return permissionGateway.check();
    }

}
