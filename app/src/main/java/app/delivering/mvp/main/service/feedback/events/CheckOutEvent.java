package app.delivering.mvp.main.service.feedback.events;

import app.core.checkin.user.post.entity.CheckInResponse;

public class CheckOutEvent {

    private CheckInResponse checkInResponse;

    public CheckOutEvent() { }

    public CheckInResponse getCheckInResponse() {
        return checkInResponse;
    }

    public void setCheckInResponse(CheckInResponse checkInResponse) {
        this.checkInResponse = checkInResponse;
    }
}
