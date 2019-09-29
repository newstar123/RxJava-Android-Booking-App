package app.delivering.mvp.verify.binder.init;

public interface LockButtonTimerInterface {

    void saveDeviceTimeInSec(int timerVal);

    int getCurrTimeValInSec();

    int getSavedDeviceTimeInSec();

    void saveTimerValInSec(int timerVal);

    int getSavedTimerValInSec();

}
