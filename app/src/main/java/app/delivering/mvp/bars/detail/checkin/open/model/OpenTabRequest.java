package app.delivering.mvp.bars.detail.checkin.open.model;

import app.core.checkin.user.post.entity.CheckInRequest;

public class OpenTabRequest {
    private boolean isIgnoreAnotherCheckIns;
    private CheckInRequest checkinRequest;
    private boolean isIgnoreBluetoothState;

    public boolean isIgnoreAnotherCheckIns() {
        return isIgnoreAnotherCheckIns;
    }

    public void setIgnoreAnotherCheckIns(boolean ignoreAnotherCheckIns) {
        isIgnoreAnotherCheckIns = ignoreAnotherCheckIns;
    }

    public void setCheckinRequest(CheckInRequest checkinRequest) {
        this.checkinRequest = checkinRequest;
    }

    public CheckInRequest getCheckinRequest() {
        return checkinRequest;
    }

    public boolean isIgnoreBluetoothState() {
        return isIgnoreBluetoothState;
    }

    public void setIgnoreBluetoothState(boolean ignoreBluetoothState) {
        isIgnoreBluetoothState = ignoreBluetoothState;
    }
}
