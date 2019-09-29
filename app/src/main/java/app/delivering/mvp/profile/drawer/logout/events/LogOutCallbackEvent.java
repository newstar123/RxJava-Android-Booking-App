package app.delivering.mvp.profile.drawer.logout.events;


public class LogOutCallbackEvent {
    private boolean isCheckOutCallBack;

    public LogOutCallbackEvent(boolean isCheckOutCallBack) {
        this.isCheckOutCallBack = isCheckOutCallBack;
    }

    public boolean isCheckOutCallBack() {
        return isCheckOutCallBack;
    }
}
