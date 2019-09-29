package app.delivering.component.bar.detail.action;

public enum BarDetailActionButtonState {
    UBER_CALL(0),
    CLOSED(1),
    OPEN_TAB(2),
    VIEW_TAB(3);

    private int actionId;

    BarDetailActionButtonState(int actionId) {
        this.actionId = actionId;
    }

    public static BarDetailActionButtonState getById(int id) {
        for(BarDetailActionButtonState e : values()) {
            if(e.actionId == id) return e;
        }
        return UBER_CALL;
    }

    public int getActionId() {
        return actionId;
    }
}
