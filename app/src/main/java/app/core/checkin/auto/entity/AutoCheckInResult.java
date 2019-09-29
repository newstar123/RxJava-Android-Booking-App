package app.core.checkin.auto.entity;

import app.core.bars.list.get.entity.BarModel;
import app.core.checkin.user.post.entity.CheckInResponse;

public class AutoCheckInResult {
    private CheckInResponse checkIn;
    private boolean isAutoOpeningSettingOn;
    private BarModel venue;

    public void setAutoOpeningSettingOn(boolean autoOpeningSettingOn) {
        this.isAutoOpeningSettingOn = autoOpeningSettingOn;
    }

    public void setCheckIn(CheckInResponse checkIn) {
        this.checkIn = checkIn;
    }

    public CheckInResponse getCheckIn() {
        return checkIn;
    }

    public boolean isAutoOpeningSettingOn() {
        return isAutoOpeningSettingOn;
    }

    public BarModel getVenue() {
        return venue;
    }

    public void setVenue(BarModel venue) {
        this.venue = venue;
    }
}
