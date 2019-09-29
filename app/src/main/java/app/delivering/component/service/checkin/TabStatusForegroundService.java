package app.delivering.component.service.checkin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class TabStatusForegroundService extends Service {
    public static String START_FOREGROUND_ACTION = "app.delivering.component.service.foreground.START_FOREGROUND";
    public static String STOP_FOREGROUND_ACTION = "app.delivering.component.service.foreground.STOP_FOREGROUND";
    public static String VIEW_CHECK_IN = "app.delivering.component.service.foreground.VIEW_CHECK_IN";

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }
}
