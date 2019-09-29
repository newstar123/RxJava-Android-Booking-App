package app.delivering.mvp.bars.detail.checkin.signup.events;

public class SignUpFromBarDetailEvent {
    private boolean isOpenTabCall;

    public SignUpFromBarDetailEvent(boolean isOpenTabCall) {
        this.isOpenTabCall = isOpenTabCall;
    }

    public boolean isOpenTabCall() {
        return isOpenTabCall;
    }
}
