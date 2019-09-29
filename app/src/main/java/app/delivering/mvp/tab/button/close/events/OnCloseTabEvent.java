package app.delivering.mvp.tab.button.close.events;

public class OnCloseTabEvent {
    private boolean isClosedByUberCall;

    public OnCloseTabEvent(boolean isClosedByUberCall) {
        this.isClosedByUberCall = isClosedByUberCall;
    }

    public boolean isClosedByUberCall() {
        return isClosedByUberCall;
    }
}
