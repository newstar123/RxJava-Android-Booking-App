package app.core.uber.start.entity;


public class ApplyRidePromoRequest {
    private Long checkinId;
    private long rideId;

    public ApplyRidePromoRequest(Long checkinId) {
        this.checkinId = checkinId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public long getRideId() {
        return rideId;
    }

    public Long getCheckinId() {
        return checkinId;
    }
}
