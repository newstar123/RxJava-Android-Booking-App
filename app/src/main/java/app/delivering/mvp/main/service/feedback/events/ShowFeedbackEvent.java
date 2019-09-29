package app.delivering.mvp.main.service.feedback.events;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class ShowFeedbackEvent {
    private GetCheckInsResponse lastSavedCheckIn;

    public ShowFeedbackEvent(GetCheckInsResponse lastSavedCheckIn) {

        this.lastSavedCheckIn = lastSavedCheckIn;
    }

    public GetCheckInsResponse getLastSavedCheckIn() {
        return lastSavedCheckIn;
    }

    public void setLastSavedCheckIn(GetCheckInsResponse lastSavedCheckIn) {
        this.lastSavedCheckIn = lastSavedCheckIn;
    }
}
