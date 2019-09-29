package app.delivering.mvp.bars.detail.checkin.click.events;

public class OpenTabClickEvent {
    private long currentBarId;
    private boolean isIgnoreAnotherCheckIns;

    public long getCurrentBarId() {
        return currentBarId;
    }

    public void setCurrentBarId(long currentBarId) {
        this.currentBarId = currentBarId;
    }

    public boolean isIgnoreAnotherCheckIns() {
        return isIgnoreAnotherCheckIns;
    }

    public void setIgnoreAnotherCheckIns(boolean ignoreAnotherCheckIns) {
        isIgnoreAnotherCheckIns = ignoreAnotherCheckIns;
    }
}
