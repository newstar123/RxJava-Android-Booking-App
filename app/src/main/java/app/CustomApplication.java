package app;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import org.altbeacon.beacon.BeaconManager;

import java.util.Arrays;

import app.delivering.component.service.beacon.broadcast.BackgroundBroadcastReceiver;
import app.delivering.component.service.beacon.broadcast.RangeCheckInReceiver;
import app.delivering.component.service.beacon.receiver.BluetoothStateReceiver;
import app.delivering.component.service.beacon.scaner.AltBeaconScanner;
import app.delivering.component.service.checkin.BackgroundCheckInController;
import app.delivering.component.service.checkin.ControlCheckInInterface;
import app.delivering.mvp.advert.ride.start.binder.subbinder.RideTrackingServiceSubBinder;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import retrofit2.Retrofit;
import rx.Observable;
import zendesk.core.Zendesk;
import zendesk.support.Support;

public class CustomApplication extends Application {
    private static CustomApplication application;
    private BackgroundBroadcastReceiver backgroundBroadcastReceiver;
    private MixpanelAPI mixpanelInstance;
    private ControlCheckInInterface checkInController;
    private Retrofit retrofit;
    private BeaconManager beaconManager;
    private RangeCheckInReceiver rangeCheckInReceiver;
    private BluetoothStateReceiver bluetoothStateReceiver;
    private AltBeaconScanner altBeaconScanner;

    @Override
    public void onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        application = this;
        FacebookSdk.sdkInitialize(this);
        setUpRestClient();
        setUpAutoCheckInController();
        initUberSession();
        initReceivers();
        restoreRideTracking();
        initBranch();
        initAnalytics();
        initZendesk();
        initBeacons();
    }

    public void setUpRestClient() {
        retrofit = QorumHttpClient.create();
    }

    public void setUpAutoCheckInController() {
        checkInController = new BackgroundCheckInController();
    }

    private void restoreRideTracking() {
        Observable.just((String)QorumSharedCache.checkUberRideId().get(BaseCacheType.STRING))
                .filter(rideId -> !TextUtils.isEmpty(rideId))
                .subscribe(RideTrackingServiceSubBinder::launch);
    }

    private void initReceivers() {
        bluetoothStateReceiver = new BluetoothStateReceiver();
        IntentFilter bluetoothIntentFilter = new IntentFilter();
        bluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, bluetoothIntentFilter);
        backgroundBroadcastReceiver = new BackgroundBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundBroadcastReceiver.STOP_SERVICE_INTENT);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(BackgroundBroadcastReceiver.CLEAR_NOTIFICATION_FILTER_ACTION);
        registerReceiver(backgroundBroadcastReceiver, intentFilter);
        rangeCheckInReceiver = new RangeCheckInReceiver();
        IntentFilter checkInFilter = new IntentFilter();
        checkInFilter.addAction(RangeCheckInReceiver.ON_CHECK_IN_ACTION);
        checkInFilter.addAction(RangeCheckInReceiver.ON_CHECK_OUT_ACTION);
        checkInFilter.addAction(RangeCheckInReceiver.ON_RADIUS_CROSSED);
        registerReceiver(rangeCheckInReceiver, checkInFilter);
    }

    public void initUberSession() {
        SessionConfiguration.Builder builder = new SessionConfiguration.Builder()
                .setClientId(getString(R.string.uber_client_id))
                .setRedirectUri("qorum://uber_redirect")
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS, Scope.REQUEST));
        SessionConfiguration sessionConfiguration = ApplicationConfig.setUpUberSessionConfig(builder);
        UberSdk.initialize(sessionConfiguration);
    }

    private void initBranch() {
        // Branch logging for debugging
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }

    private void initAnalytics() {
        mixpanelInstance = MixpanelAPI.getInstance(this, getString(R.string.mixpanel));
    }

    private void initZendesk() {
        Zendesk.INSTANCE.init(this, getString(R.string.zendesk_url),
                getString(R.string.zendesk_app_id), getString(R.string.zendesk_client_id));
        Support.INSTANCE.init(Zendesk.INSTANCE);
    }

    private void initBeacons() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        altBeaconScanner = new AltBeaconScanner(this, beaconManager);
        beaconManager.bind(altBeaconScanner);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static CustomApplication get() {
        return application;
    }

    @Override public void onTerminate() {
        super.onTerminate();
        beaconManager.unbind(altBeaconScanner);
        unregisterReceiver(backgroundBroadcastReceiver);
        unregisterReceiver(rangeCheckInReceiver);
        unregisterReceiver(bluetoothStateReceiver);
    }

    public MixpanelAPI getMixpanelInstance() {
        return mixpanelInstance;
    }

    public ControlCheckInInterface getCheckInController() {
        return checkInController;
    }

    public Retrofit getRestClient(){
        return retrofit;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }
}
