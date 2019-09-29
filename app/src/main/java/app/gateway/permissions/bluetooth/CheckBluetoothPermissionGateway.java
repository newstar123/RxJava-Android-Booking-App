package app.gateway.permissions.bluetooth;

import android.Manifest;

import com.tbruyelle.rxpermissions.RxPermissions;

import app.core.permission.bluetooth.entity.BluetoothPermissionException;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;

public class CheckBluetoothPermissionGateway implements CheckPermissionGateway {
    private BaseActivity activity;

    public CheckBluetoothPermissionGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Boolean> check() {
        return new RxPermissions(activity)
                .request(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .doOnNext(isGranted -> {
                    if (!isGranted)
                        throw new BluetoothPermissionException();
                });
    }
}
