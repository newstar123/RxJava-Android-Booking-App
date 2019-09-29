package app.qamode.activation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;

import app.BuildConfig;
import app.CustomApplication;
import app.delivering.component.BaseActivity;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;

public class QaModeActivationListener implements QaModeListenerInterface{
    private SensorManager sensor;
    private float shakeSpeed;
    private float shakeSpeedCurrent;
    private float shakeSpeedLast;
    private int shakeCounter;

    public QaModeActivationListener(BaseActivity activity) {
        if (BuildConfig.DEBUG)
            sensor = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onStart() {
        if (sensor != null) {
            sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
            shakeSpeedCurrent = SensorManager.GRAVITY_EARTH;
            shakeSpeedLast = SensorManager.GRAVITY_EARTH;
            shakeCounter = 0;
        }
    }

    @Override
    public void onStop() {
        if (sensor != null) {
            sensor.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        shakeSpeedLast = shakeSpeedCurrent;
        shakeSpeedCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = shakeSpeedCurrent - shakeSpeedLast;
        shakeSpeed = shakeSpeed * 0.9f + delta;
        if (shakeSpeed > 0.1) {
            if (shakeSpeed > 12) {
                shakeCounter++;
                if (shakeCounter >= 3 && !(Boolean) QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN)){
                    QaModeCache.isQaModeActive().save(BaseCacheType.BOOLEAN, true);
                    Toast.makeText(CustomApplication.get(), "QA Mode is active. \n" +
                            "Please, set up it on your profile.", Toast.LENGTH_SHORT).show();
                }
            } else if (shakeSpeed < 2) {
                shakeCounter = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
