package app.delivering.mvp.tab.summary.init.model;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class RefreshCheckInEvent {
    private GetCheckInsResponse checkIn;

    public GetCheckInsResponse getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(GetCheckInsResponse checkIn) {
        this.checkIn = checkIn;
    }
}
