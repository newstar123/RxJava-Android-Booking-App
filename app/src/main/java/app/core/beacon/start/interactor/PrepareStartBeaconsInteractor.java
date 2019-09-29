package app.core.beacon.start.interactor;

import app.core.BaseOutputInteractor;
import app.core.beacon.bluetooth.interactor.CheckBluetoothInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.core.permission.bluetooth.interactor.BluetoothPermissionInteractor;
import app.delivering.component.BaseActivity;
import rx.Observable;

public class PrepareStartBeaconsInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private CheckBluetoothInteractor checkBluetoothInteractor;
    private BluetoothPermissionInteractor bluetoothPermissionInteractor;

    public PrepareStartBeaconsInteractor(BaseActivity activity){
        checkBluetoothInteractor = new CheckBluetoothInteractor();
        bluetoothPermissionInteractor = new BluetoothPermissionInteractor(activity);
    }

    @Override public Observable<EmptyResponse> process() {
        return bluetoothPermissionInteractor.process()
                .concatMap(isOk -> checkBluetoothInteractor.process());
    }
}
