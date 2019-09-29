package app.delivering.component.main.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;

public class EnableBluetoothActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        findViewById(R.id.enable_bluetooth_back_button).setOnClickListener(v -> finish());
        findViewById(R.id.enable_bluetooth_button).setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null)
                bluetoothAdapter.enable();
            finish();
        });
    }

    @Override
    public void onBackPressed() {}
}
