package app.core.checkout.entity;

public class CheckOutRequestBody {
    private long checkInId;
    private boolean closedByUberRequest;

    public CheckOutRequestBody(long checkInId, boolean closedByUberRequest) {
        this.checkInId = checkInId;
        this.closedByUberRequest = closedByUberRequest;
    }

    public long getCheckInId() {
        return checkInId;
    }

    public boolean isClosedByUberRequest() {
        return closedByUberRequest;
    }
}
