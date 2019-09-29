package app.delivering.mvp.main.init.events;

public class OpenVenueFromBranchEvent {
    private int venueName;

    public OpenVenueFromBranchEvent(int venueName) {
        this.venueName = venueName;
    }

    public int getBarNameFromBranch() {
        return venueName;
    }
}
