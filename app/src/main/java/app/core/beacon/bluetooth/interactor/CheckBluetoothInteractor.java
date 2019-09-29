package app.core.beacon.bluetooth.interactor;

import app.core.BaseOutputInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.bluetooth.CheckBluetoothActiveGateway;
import rx.Observable;

public class CheckBluetoothInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private CheckBluetoothActiveGateway bluetoothActiveGateway;

    public CheckBluetoothInteractor(){
        bluetoothActiveGateway = new CheckBluetoothActiveGateway();
    }

    @Override public Observable<EmptyResponse> process() {
        return bluetoothActiveGateway.check();
    }
}
