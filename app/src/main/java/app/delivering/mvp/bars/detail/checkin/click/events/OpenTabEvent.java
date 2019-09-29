package app.delivering.mvp.bars.detail.checkin.click.events;

public class OpenTabEvent {
    private long currentBarId;
    private boolean isIgnoreAnotherCheckIns;
    private boolean isIgnoreBluetoothState;

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

    public boolean isIgnoreBluetoothState() {
        return isIgnoreBluetoothState;
    }

    public void setIgnoreBluetoothState(boolean ignoreBluetoothState) {
        isIgnoreBluetoothState = ignoreBluetoothState;
    }

}
