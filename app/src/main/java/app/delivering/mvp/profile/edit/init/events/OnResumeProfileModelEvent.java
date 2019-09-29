package app.delivering.mvp.profile.edit.init.events;

public class OnResumeProfileModelEvent {
    private String number;

    public OnResumeProfileModelEvent(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

}
