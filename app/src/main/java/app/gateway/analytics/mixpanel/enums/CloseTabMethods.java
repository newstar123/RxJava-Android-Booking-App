package app.gateway.analytics.mixpanel.enums;

public enum CloseTabMethods {
    CLOSE_TAB("Manually Tap Close Tab Button"),
    CLOSE_TAB_BY_BEACON("Auto-Close with Beacons"),
    CLOSE_TAB_BY_BARTENDER("Manually Closed by Bartender"),
    CLOSE_TAB_BY_UBER_CALL("Manually Tap Uber Request Button");

    private String name;

    CloseTabMethods(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
