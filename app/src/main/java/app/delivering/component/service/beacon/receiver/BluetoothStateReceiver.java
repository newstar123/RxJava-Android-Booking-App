package app.delivering.component.service.beacon.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import app.CustomApplication;
import app.delivering.component.service.beacon.BeaconService;

public class BluetoothStateReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action != null || !action.isEmpty()) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
                setUpBeaconLocator(intent);
        }
    }

    private void setUpBeaconLocator(Intent intent) {
        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF)
            stopBeaconLocator();

        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON)
            startBeaconLocator();
    }

    private void stopBeaconLocator() {
        Log.d(BeaconService.TRACKING_TAG, "BluetoothStateReceiver - stopBeaconLocator");
        if (CustomApplication.get().getCheckInController() != null)
            CustomApplication.get().getCheckInController().stopCheckInScanner();
    }

    private void startBeaconLocator() {
        Log.d(BeaconService.TRACKING_TAG, "BluetoothStateReceiver - startBeaconLocator");
        if (CustomApplication.get().getCheckInController() != null)
            CustomApplication.get().getCheckInController().activateCheckInScanner();
    }

}
