package app.delivering.component.ride.mock;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import app.delivering.mvp.ride.mock.MockRideBinder;

public class MockRideService extends Service {
    public static final String UBER_RIDE_ID_KEY = "UBER_RIDE_ID_KEY";
    private MockRideBinder mockRideBinder;

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();
        if (mockRideBinder == null)
            mockRideBinder = new MockRideBinder(this);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent);
        return START_REDELIVER_INTENT;
    }

    private void start(Intent intent) {
        String uberRideId = intent.getStringExtra(UBER_RIDE_ID_KEY);
        mockRideBinder.process(uberRideId);
    }
}
