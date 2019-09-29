package app.delivering.component.service.beacon.scaner;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.CustomApplication;
import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.core.bars.list.get.entity.BeaconModel;
import app.delivering.mvp.beacons.invitation.events.CheckInInvitationEvent;
import app.delivering.mvp.beacons.scanner.BeaconInitScannerPresenter;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

public class AltBeaconScanner implements BeaconConsumer, MonitorNotifier, RangeNotifier {
    private static final int SCAN_PERIOD = 1000;
    private static final int BETWEEN_SCAN_PERIOD = 2000;
    private static final int BACKGROUND_BETWEEN_SCAN_PERIOD = 10000;
    private static final int TRACKING_AGE = 20000;
    private static final String IBEACON_KEY = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String IBEACON_KEY_2 = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String COMMON_UUID = "96F181EB-AB05-4211-BADF-2ACABD7875F3";
    private static final String COMMON_UU = "23400000-0000-0000-0000-000000000000";
    private Context context;
    private BeaconManager beaconManager;
    private final BeaconInitScannerPresenter scannerPresenter;
    private List<BarModel> barModelList;
    private BarModel rangedBarModel;
    private boolean isExitRegionActivated;

    public AltBeaconScanner(Context context, BeaconManager beaconManager) {
        this.context = context;
        this.beaconManager = beaconManager;
        setUpBeaconManager();
        setBeaconLayouts();
        scannerPresenter = new BeaconInitScannerPresenter(context);
        barModelList = new ArrayList<>();

        LogToFileHandler.addLog("Beacon Scanner created");
    }

    private void setUpBeaconManager() {
        beaconManager.setForegroundScanPeriod(SCAN_PERIOD);
        beaconManager.setForegroundBetweenScanPeriod(BETWEEN_SCAN_PERIOD);
        beaconManager.setBackgroundScanPeriod(SCAN_PERIOD);
        beaconManager.setBackgroundBetweenScanPeriod(BACKGROUND_BETWEEN_SCAN_PERIOD);
        BeaconManager.setUseTrackingCache(true);
        beaconManager.setMaxTrackingAge(TRACKING_AGE);
    }

    private void setBeaconLayouts() {
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_KEY));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_KEY_2));
    }

    @Override
    public void onBeaconServiceConnect() {
        LogToFileHandler.addLog("Beacon Service connected");
        beaconManager.addRangeNotifier(this);
        beaconManager.addMonitorNotifier(this);
        scannerPresenter.process().subscribe(this::addRegions, e -> {
                    QorumNotifier.notify(context, NotificationType.BEACON_ERROR, new HashMap());
                }, () -> {});
    }

    private void addRegions(List<BarModel> barModels) {
        barModelList = barModels;
        try {
            beaconManager.startMonitoringBeaconsInRegion(getCommonRegion());
        } catch (RemoteException e) {
            LogToFileHandler.addLog("Beacon Scanning initial error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        LogToFileHandler.addLog("Beacon Service didEnterRegion");
        startRanging();
    }

    @Override
    public void didExitRegion(Region region) {
        LogToFileHandler.addLog("Beacon Service didExitRegion");
        for (BarModel bar : barModelList)
            if (bar == rangedBarModel) {
            isExitRegionActivated = true;
                Observable.timer(TRACKING_AGE, TimeUnit.MILLISECONDS)
                        .subscribe(tick -> {
                            if (isExitRegionActivated) {
                                resetSavedScanningParams();
                                QorumNotifier.clearNotification(getApplicationContext(), NotificationType.BEACON_TAB_OPENING);
                            }
                        }, e->{}, ()->{});
            }
    }

    private void resetSavedScanningParams() {
        rangedBarModel = null;
        isExitRegionActivated = false;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.isEmpty()) return;

        BarModel barModel = findBarAccordance(beacons);
        if (barModel != null && barModel != rangedBarModel) {
            Beacon nearestBeacon = getNearestBeacon(beacons);
            if (nearestBeacon != null) {
                try {
                    int beaconId = Integer.parseInt(nearestBeacon.getId3().toString());
                    rangeRegionFor(barModel, beaconId, nearestBeacon.getDistance());
                } catch (NullPointerException | NumberFormatException e) {
                    LogToFileHandler.addLog("Beacon parsing error - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private BarModel findBarAccordance(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons){
            try {
                int barId = Integer.parseInt(beacon.getId2().toString());
                for (BarModel model : barModelList)
                    if (model.getId() == barId) return model;
            } catch (NullPointerException | NumberFormatException e) {
                LogToFileHandler.addLog("Beacon parsing error - " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private Beacon getNearestBeacon(Collection<Beacon> beacons) {
        Beacon nearestBeacon = null;
        for (Beacon beacon : beacons){
            if (nearestBeacon == null) nearestBeacon = beacon;
            else if (beacon.getDistance() < nearestBeacon.getDistance()) nearestBeacon = beacon;
        }
        return nearestBeacon;
    }

    private void rangeRegionFor(BarModel bar, int beaconId, double distance) {
        LogToFileHandler.addLog("Beacon Service rangeRegionFor-" + bar.getName() + ". Distance-" + distance + ". beaconId-" + beaconId);
        for (BeaconModel model : bar.getBeacons()){
            sendInviteMessage(bar);
                LogToFileHandler.addLog("Beacon Service rangeRegion. Distance=" + distance + " Radius=" + model.getAttributes().getCheckinRadius());
                //compare radius to check in
                if (distance <= model.getAttributes().getCheckinRadius()) {
                    rangedBarModel = bar;
                    CustomApplication.get().getCheckInController().onCheckInRadiusCrossed(true, rangedBarModel);
                } else {
                    resetSavedScanningParams();
                }
        }
    }

    private void sendInviteMessage(BarModel model) {
        EventBus.getDefault().post(new CheckInInvitationEvent(model.getId(), model.getName()));
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        resetRanging();
        LogToFileHandler.addLog("Beacon Service didDetermineStateForRegion. State-" + i);
        if (i == 1)
            startRanging();
    }

    private void startRanging() {
        try {
            beaconManager.startRangingBeaconsInRegion(getCommonRegion());
            isExitRegionActivated = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        resetRanging();
        LogToFileHandler.addLog("Beacon Service disconnected");
        context.unbindService(serviceConnection);
    }

    private void resetRanging() {
        try {
            beaconManager.stopRangingBeaconsInRegion(getCommonRegion());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Region getCommonRegion() {
        return new Region(context.getString(R.string.app_name), Identifier.parse(COMMON_UUID), null, null);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }
}
