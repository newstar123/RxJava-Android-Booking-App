package app.gateway.permissions.bluetooth.context;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import app.core.permission.entity.LocationPermissionException;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;

public class CheckBluetoothContextPermissionGateway implements CheckPermissionGateway {
    private Context context;

    public CheckBluetoothContextPermissionGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Boolean> check() {
        return Observable.create(subscriber -> {
            int bluetoothState = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            int bluetoothAdminState = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN);
            if (bluetoothState == context.getPackageManager().PERMISSION_GRANTED && bluetoothAdminState == context.getPackageManager().PERMISSION_GRANTED){
                subscriber.onNext(true);
                subscriber.onCompleted();
            } else
                subscriber.onError(new LocationPermissionException());
        });
    }
}
