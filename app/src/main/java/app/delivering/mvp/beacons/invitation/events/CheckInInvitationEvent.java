package app.delivering.mvp.beacons.invitation.events;

public class CheckInInvitationEvent {
    private long id;
    private String name;

    public CheckInInvitationEvent(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
