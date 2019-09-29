package app;


import com.uber.sdk.rides.client.SessionConfiguration;

public class ApplicationConfig {
    public static String UBER_URL = "https://api.uber.com/";

    public static SessionConfiguration setUpUberSessionConfig(SessionConfiguration.Builder builder) {
        return builder.build();
    }

    public static void launchMockRide(String rideId){
    }

}
