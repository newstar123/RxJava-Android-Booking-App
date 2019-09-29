package app.delivering.mvp.beacons.service.binder;

import android.app.Service;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import app.delivering.component.service.beacon.broadcast.model.StopForegroundServiceEvent;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.beacons.service.events.StartBeaconsForegroundEvent;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;

public class BeaconsForegroundBinder extends BaseServiceBinder {

    public BeaconsForegroundBinder(Service service) {
        super(service);
    }

    @Subscribe
    public void startForeground(StartBeaconsForegroundEvent event) {
        QorumNotifier.addChannel(getService(), NotificationType.BEACON_SCANNING.getChanelID());
        getService().startForeground(NotificationType.BEACON_SCANNING.getNotificationID(),
                QorumNotifier.getNotification(getService(),
                        NotificationType.BEACON_SCANNING,
                        new HashMap<>()));
    }

    @Subscribe
    public void stopForeground(StopForegroundServiceEvent event) {
        if (event.getNotificationId() == NotificationType.BEACON_SCANNING.getNotificationID())
            getService().stopForeground(true);
    }
}
