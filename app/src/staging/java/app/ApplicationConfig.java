package app;


import android.content.Intent;

import com.uber.sdk.rides.client.SessionConfiguration;

import app.delivering.component.ride.mock.MockRideService;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;

public class ApplicationConfig {

    public static SessionConfiguration setUpUberSessionConfig(SessionConfiguration.Builder builder) {
        if (isQAModeWithActiveLiveUber())
                return builder.build();
        return builder.setEnvironment(SessionConfiguration.Environment.SANDBOX).build();
    }

    public static void launchMockRide(String rideId){
        if (!isQAModeWithActiveLiveUber()) {
            Intent mockServiceIntent = new Intent(CustomApplication.get(), MockRideService.class);
            mockServiceIntent.putExtra(MockRideService.UBER_RIDE_ID_KEY, rideId);
            CustomApplication.get().startService(mockServiceIntent);
        }
    }

    private static boolean isQAModeWithActiveLiveUber() {
        if (QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN))
            if (QaModeCache.getQaModeUberApiState().get(BaseCacheType.BOOLEAN))
                return true;
        return false;
    }
}
