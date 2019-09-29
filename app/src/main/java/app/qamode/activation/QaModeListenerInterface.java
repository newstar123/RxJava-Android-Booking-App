package app.qamode.activation;

import android.hardware.SensorEventListener;

public interface QaModeListenerInterface extends SensorEventListener {

    void onStart();
    void onStop();

}
