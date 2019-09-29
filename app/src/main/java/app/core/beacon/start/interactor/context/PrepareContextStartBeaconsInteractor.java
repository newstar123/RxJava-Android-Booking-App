package app.core.beacon.start.interactor.context;

import android.content.Context;

import app.core.BaseOutputInteractor;
import app.core.beacon.bluetooth.interactor.CheckBluetoothInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.core.permission.bluetooth.interactor.context.BluetoothPermissionWithContextInteractor;
import rx.Observable;

public class PrepareContextStartBeaconsInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private CheckBluetoothInteractor checkBluetoothInteractor;
    private BluetoothPermissionWithContextInteractor bluetoothPermissionInteractor;

    public PrepareContextStartBeaconsInteractor(Context context){
        checkBluetoothInteractor = new CheckBluetoothInteractor();
        bluetoothPermissionInteractor = new BluetoothPermissionWithContextInteractor(context);
    }

    @Override public Observable<EmptyResponse> process() {
        return bluetoothPermissionInteractor.process()
                .concatMap(isOk -> checkBluetoothInteractor.process());
    }
}
