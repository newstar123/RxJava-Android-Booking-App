package app.delivering.mvp.main.service.init.model;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class ActivateServicesResult {
    private GetCheckInsResponse activeCheckIn;
    private GetCheckInsResponse lastSavedCheckIn;
    private boolean isLatestCheckInSaved;
    private GetCheckInsResponse checkOut;

    public void setActiveCheckIn(GetCheckInsResponse activeCheckIn) {
        this.activeCheckIn = activeCheckIn;
    }

    public void setLastSavedCheckIn(GetCheckInsResponse lastSavedCheckIn) {
        this.lastSavedCheckIn = lastSavedCheckIn;
    }

    public GetCheckInsResponse getActiveCheckIn() {
        return activeCheckIn;
    }

    public GetCheckInsResponse getLastSavedCheckIn() {
        return lastSavedCheckIn;
    }

    public void setLatestCheckInSaved(boolean isLatestCheckInSaved) {
        this.isLatestCheckInSaved = isLatestCheckInSaved;
    }

    public boolean isLatestCheckInSaved() {
        return isLatestCheckInSaved;
    }

    public void setCheckOut(GetCheckInsResponse checkOut) {
        this.checkOut = checkOut;
    }

    public GetCheckInsResponse getCheckOut() {
        return checkOut;
    }
}
