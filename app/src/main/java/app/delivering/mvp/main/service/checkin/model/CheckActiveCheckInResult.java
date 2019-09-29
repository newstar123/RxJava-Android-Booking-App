package app.delivering.mvp.main.service.checkin.model;

public class CheckActiveCheckInResult {
    private long activeCheckInId;
    private long barId;
    private String barName;
    private String barImage;

    public long getActiveCheckInId() {
        return activeCheckInId;
    }

    public void setActiveCheckInId(long activeCheckInId) {
        this.activeCheckInId = activeCheckInId;
    }

    public long getBarId() {
        return barId;
    }

    public void setBarId(long barId) {
        this.barId = barId;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public String getBarImage() {
        return barImage;
    }

    public void setBarImage(String barImage) {
        this.barImage = barImage;
    }
}
