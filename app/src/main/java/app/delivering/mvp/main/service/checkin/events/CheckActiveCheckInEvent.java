package app.delivering.mvp.main.service.checkin.events;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class CheckActiveCheckInEvent {
    private GetCheckInsResponse activeCheckIn;

    public CheckActiveCheckInEvent(GetCheckInsResponse activeCheckIn) {

        this.activeCheckIn = activeCheckIn;
    }

    public GetCheckInsResponse getActiveCheckIn() {
        return activeCheckIn;
    }

    public void setActiveCheckIn(GetCheckInsResponse activeCheckIn) {
        this.activeCheckIn = activeCheckIn;
    }
}
