package app.delivering.component.service.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.altbeacon.beacon.BeaconManager;
import org.greenrobot.eventbus.EventBus;

import app.CustomApplication;
import app.delivering.mvp.beacons.invitation.binder.CheckInInvitationBinder;
import app.delivering.mvp.beacons.service.binder.BeaconsForegroundBinder;
import app.delivering.mvp.beacons.service.events.StartBeaconsForegroundEvent;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.qamode.log.LogToFileHandler;

public class BeaconService extends Service {
    public static final String TRACKING_TAG = "TRACKING_TAG";
    public static String START_FOREGROUND_ACTION = "app.delivering.component.service.beacon.START_FOREGROUND";
    public static String STOP_FOREGROUND_ACTION = "app.delivering.component.service.beacon.STOP_FOREGROUND";
    private BeaconManager beaconManager;
    private BeaconsForegroundBinder foregroundBinder;
    private CheckInInvitationBinder invitationBinder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (beaconManager == null)
            beaconManager = CustomApplication.get().getBeaconManager();
        if (beaconManager != null && !beaconManager.isMainProcess())
            beaconManager.setBackgroundMode(false);
        foregroundBinder = new BeaconsForegroundBinder(this);
        EventBus.getDefault().register(foregroundBinder);
        invitationBinder = new CheckInInvitationBinder(this);
        EventBus.getDefault().register(invitationBinder);
        LogToFileHandler.addLog("Beacon Service instance - " + this.hashCode());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!TextUtils.isEmpty(intent.getAction()))
            checkIntentType(intent);
        return START_REDELIVER_INTENT;
    }

    private void checkIntentType(Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(STOP_FOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        } else {
            start();
        }
    }

    private void start() {
        QorumNotifier.clearNotification(this, NotificationType.BEACON_TAB_OPENING);
        QorumNotifier.clearNotification(this, NotificationType.BEACON_ERROR);
        EventBus.getDefault().post(new StartBeaconsForegroundEvent());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(foregroundBinder);
        EventBus.getDefault().unregister(invitationBinder);
        super.onDestroy();
    }
}
