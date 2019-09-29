package app.gateway.analytics.mixpanel.enums;

public enum OpenTabMethods {
    OPEN_TAB("Manually Tap Open Tab Button"),
    OPEN_TAB_BY_BEACON("Auto-Open with Beacons");

    private String name;

    OpenTabMethods(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
