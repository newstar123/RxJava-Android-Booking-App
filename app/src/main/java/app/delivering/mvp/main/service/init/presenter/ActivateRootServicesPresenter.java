package app.delivering.mvp.main.service.init.presenter;

import android.bluetooth.BluetoothAdapter;

import app.CustomApplication;
import app.core.service.ActivateRootServiceInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.main.init.presenter.BluetoothStateListener;
import app.delivering.mvp.main.service.init.model.ActivateServicesResult;
import rx.Observable;

public class ActivateRootServicesPresenter extends BaseOutputPresenter<Observable<ActivateServicesResult>> implements BluetoothStateListener {
    private ActivateRootServiceInteractor rootServiceInteractor;

    public ActivateRootServicesPresenter(BaseActivity activity) {
        super(activity);
        rootServiceInteractor = new ActivateRootServiceInteractor(activity);
    }

    @Override public Observable<ActivateServicesResult> process() {
        return rootServiceInteractor.process();
    }

    private boolean isBluetoothOn() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON;
    }

    @Override
    public void setUpBluetoothScanner() {
        if (isBluetoothOn() && CustomApplication.get().getCheckInController() != null)
            CustomApplication.get().getCheckInController().activateCheckInScanner();
    }
}
