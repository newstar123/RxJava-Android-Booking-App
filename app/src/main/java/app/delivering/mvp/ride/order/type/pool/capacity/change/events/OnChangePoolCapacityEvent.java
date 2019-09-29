package app.delivering.mvp.ride.order.type.pool.capacity.change.events;


public class OnChangePoolCapacityEvent {
    private int checkedId;

    public OnChangePoolCapacityEvent(int checkedId) {
        this.checkedId = checkedId;
    }

    public int getCheckedId() {
        return checkedId;
    }
}
