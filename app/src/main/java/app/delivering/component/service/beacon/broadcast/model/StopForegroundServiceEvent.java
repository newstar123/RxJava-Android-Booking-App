package app.delivering.component.service.beacon.broadcast.model;

public class StopForegroundServiceEvent {
    private int notificationId;

    public StopForegroundServiceEvent(int notificationId) {

        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
