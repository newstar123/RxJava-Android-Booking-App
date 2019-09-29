package app.core.checkin.update;

public class CurrentCheckInInfo {

    private Long checkInId;
    private Long checkInBarId;

    public CurrentCheckInInfo(Long checkInId, Long checkInBarId) {
        this.checkInId = checkInId;
        this.checkInBarId = checkInBarId;
    }

    public CurrentCheckInInfo() {
        checkInId = 0L;
        checkInBarId = 0L;
    }

    public Long getCheckInBarId() {
        return checkInBarId;
    }

    public Long getCheckInId() {
        return checkInId;
    }
}
