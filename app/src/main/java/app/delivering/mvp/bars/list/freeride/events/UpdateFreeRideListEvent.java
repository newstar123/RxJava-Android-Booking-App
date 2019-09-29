package app.delivering.mvp.bars.list.freeride.events;

public class UpdateFreeRideListEvent {
    private boolean isFreeActivated;

    public UpdateFreeRideListEvent(boolean isFreeActivated) {
        this.isFreeActivated = isFreeActivated;
    }

    public boolean isFreeRideActivated() {
        return isFreeActivated;
    }
}
