package app.gateway.bluetooth;

import android.bluetooth.BluetoothAdapter;

import app.core.beacon.bluetooth.entity.BluetoothNotAvailableException;
import app.core.beacon.bluetooth.entity.BluetoothStateException;
import app.core.beacon.bluetooth.gateway.CheckBluetoothStateGateway;
import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public class CheckBluetoothActiveGateway implements CheckBluetoothStateGateway {

    @Override public Observable<EmptyResponse> check() {
        return Observable.create(subscriber -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                subscriber.onError(new BluetoothNotAvailableException());
            else if (!bluetoothAdapter.isEnabled() && !bluetoothAdapter.isDiscovering())
                subscriber.onError(new BluetoothStateException());
            else {
                subscriber.onNext(new EmptyResponse());
                subscriber.onCompleted();
            }
        });
    }
}
