package app.core.uber.start.entity;


import com.uber.sdk.rides.client.model.Ride;

public class StartUberRideResponse {
    private Ride body;

    public StartUberRideResponse(Ride body) {
        this.body = body;
    }

    public Ride getBody() {
        return body;
    }


}
