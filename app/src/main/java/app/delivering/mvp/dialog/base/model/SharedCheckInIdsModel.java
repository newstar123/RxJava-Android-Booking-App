package app.delivering.mvp.dialog.base.model;

public class SharedCheckInIdsModel {
    private Long checkInId;
    private Long barId;

    public SharedCheckInIdsModel(Long checkInId, Long barId) {
        this.checkInId = checkInId;
        this.barId = barId;
    }

    public Long getCheckInId() {
        return checkInId;
    }

    public Long getBarId() {
        return barId;
    }
}
