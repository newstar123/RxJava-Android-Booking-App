package app.core.uber.mock.ride.entity;


public class UberMockRideRequest {
    private String status;
    private String uberRideId;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUberRideId(String uberRideId) {
        this.uberRideId = uberRideId;
    }

    public String getUberRideId() {
        return uberRideId;
    }


}
