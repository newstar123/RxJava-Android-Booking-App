package app.core.uber.start.entity;


public class CheckRidePromoRequest {
    private Long checkinId;

    public CheckRidePromoRequest(Long checkinId) {
        this.checkinId = checkinId;
    }

    public Long getCheckinId() {
        return checkinId;
    }
}
