package app.delivering.mvp.main.photo.autoopen.events;

public class ProfileUpdatedEvent {
    private boolean needUpdate;

    public ProfileUpdatedEvent(boolean needUpdate) {this.needUpdate = needUpdate;}

    public boolean isNeedUpdate() {
        return needUpdate;
    }
}
