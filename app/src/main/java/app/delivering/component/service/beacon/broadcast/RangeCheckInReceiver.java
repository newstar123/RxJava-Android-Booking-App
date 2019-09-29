package app.delivering.component.service.beacon.broadcast;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import app.CustomApplication;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.gateway.location.current.GetCurrentRxLocationGateway;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import app.qamode.mvp.background.venuelocation.FakeVenueLocationType;
import app.qamode.qacache.QaModeCache;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class RangeCheckInReceiver extends BroadcastReceiver {
    public static final String ON_CHECK_IN_ACTION = "app.delivering.component.service.broadcast.ON_CHECK_IN";
    public static final String ON_CHECK_OUT_ACTION = "app.delivering.component.service.broadcast.ON_CHECK_OUT";
    public static final String ON_RADIUS_CROSSED = "app.delivering.component.service.broadcast.ON_RADIUS_CROSSED";
    public static final String CHECK_IN_KEY = "app.delivering.component.service.broadcast.CHECK_IN_KEY";
    private static final float DEFAULT_CHECK_OUT_RADIUS = 25;
    private static final int AUTO_UPDATE_LOCATION_TIME = 2000;
    private static final int AUTO_UPDATE_LOCATION_DISTANCE = 10;
    private PendingIntent pIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        switch (intent.getAction()) {
            case ON_CHECK_IN_ACTION:
                startCheckOutRanging(context, intent);
                break;
            case ON_CHECK_OUT_ACTION:
                stopCheckOutRanging(context);
                break;
            case ON_RADIUS_CROSSED:
                onRadiusCrossed(intent);
                break;
        }
    }

    private void startCheckOutRanging(Context context, Intent intent) {
        GetCheckInsResponse checkIn = intent.getParcelableExtra(CHECK_IN_KEY);
        if (checkIn != null && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (pIntent != null) {
                manager.removeProximityAlert(pIntent);
            }

            Intent irangeIntent = new Intent(ON_RADIUS_CROSSED);
            Bundle bundle = new Bundle();
            bundle.putParcelable(CHECK_IN_KEY, checkIn);
            irangeIntent.putExtra(CHECK_IN_KEY, bundle);

            pIntent = PendingIntent.getBroadcast(context, -1, irangeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            addProximityAlert(manager, checkIn, context);
            Location venueLocation = new Location("venue");
            venueLocation.setLatitude(checkIn.getVendor().getLatitude());
            venueLocation.setLongitude(checkIn.getVendor().getLongitude());
            // for QA mode
            if (QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AUTO_UPDATE_LOCATION_TIME,
                        AUTO_UPDATE_LOCATION_DISTANCE, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                float distance = location.distanceTo(venueLocation);
                                LogToFileHandler.addLog("Distance to the " + checkIn.getVendor().getName() + " - " + distance);
                                tryToShowDistanceMessage(context, distance, checkIn.getVendor().getName());
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
            }
            LogToFileHandler.addLog("startCheckOutRanging for checkin - " + checkIn.getId() + " in the " + checkIn.getVendor().getName());
        }
    }

    private void addProximityAlert(LocationManager manager, GetCheckInsResponse checkIn, Context context) {
        Location targetLocation = new Location(String.valueOf(checkIn.getId()));
        targetLocation.setLatitude(checkIn.getVendor().getLatitude());
        targetLocation.setLongitude(checkIn.getVendor().getLongitude());
        Observable.just(targetLocation)
                .concatMap(location -> checkQaModeSettings(location, context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> manager.addProximityAlert(location.getLatitude(), location.getLongitude(), DEFAULT_CHECK_OUT_RADIUS, -1, pIntent),
                        e-> LogToFileHandler.addLog("addProximityAlert error - " + e.getMessage()), ()->{});
    }

    private Observable<Location> checkQaModeSettings(Location location, Context context) {
        if ((boolean)QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN) && (boolean)QaModeCache.getQaModeVenueLocation().get(BaseCacheType.BOOLEAN)) {
            if (FakeVenueLocationType.toType(QaModeCache.getQaModeVenueLocationType().get(BaseCacheType.STRING)) == FakeVenueLocationType.CUSTOM){
                location.setLatitude(Double.parseDouble(QaModeCache.getQaModeVenueLocationLatitude().get(BaseCacheType.STRING)));
                location.setLongitude(Double.parseDouble(QaModeCache.getQaModeVenueLocationLongitude().get(BaseCacheType.STRING)));
            } else {
                return new GetCurrentRxLocationGateway(context).get();
            }
        }
        return Observable.just(location);
    }

    private void tryToShowDistanceMessage(Context context, float distance, String name) {
        if (QaModeCache.getQaModeAutoCheckoutDistanceVisibility().get(BaseCacheType.BOOLEAN))
            Toast.makeText(context, distance + " to " + name, Toast.LENGTH_SHORT).show();
    }

    private void stopCheckOutRanging(Context context) {
        LogToFileHandler.addLog("stopCheckOutRanging");
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && pIntent != null) manager.removeProximityAlert(pIntent);
    }

    private void onRadiusCrossed(Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        boolean isInRadius = intent.getBooleanExtra(key, false);
        Bundle bundle = intent.getBundleExtra(CHECK_IN_KEY);

        if (bundle != null) {
            GetCheckInsResponse checkIn = bundle.getParcelable(CHECK_IN_KEY);
            if (checkIn != null) {
                LogToFileHandler.addLog("on Radius 25m Crossed for checkin - " + checkIn.getId() + " in the " + checkIn.getVendor().getName());
                CustomApplication.get().getCheckInController().onCheckOutRadiusCrossed(isInRadius, pIntent, checkIn);
            }
        }
    }
}
