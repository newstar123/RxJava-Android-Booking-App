package app.delivering.mvp.bars.detail.checkin.click.events;

public class ViewTabEvent {
    private long checkinId;

    public ViewTabEvent(long checkinId) {

        this.checkinId = checkinId;
    }

    public long getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(long checkinId) {
        this.checkinId = checkinId;
    }
}
