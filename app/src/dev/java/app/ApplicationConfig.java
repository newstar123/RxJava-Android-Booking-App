package app;


import android.content.Intent;

import com.uber.sdk.rides.client.SessionConfiguration;

import app.delivering.component.ride.mock.MockRideService;

public class ApplicationConfig {
    public static String UBER_URL = "https://sandbox-api.uber.com/";

    public static SessionConfiguration setUpUberSessionConfig(SessionConfiguration.Builder builder) {
        return builder.setEnvironment(SessionConfiguration.Environment.SANDBOX).build();
    }

    public static void launchMockRide(String rideId){
        Intent mockServiceIntent = new Intent(CustomApplication.get(), MockRideService.class);
        mockServiceIntent.putExtra(MockRideService.UBER_RIDE_ID_KEY, rideId);
        CustomApplication.get().startService(mockServiceIntent);
    }
}
