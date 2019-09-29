package app.delivering.mvp.notification.notifier;

public enum NotificationType {
    BEACON_SCANNING("beacons_scanner", 10201),
    BEACON_ERROR("beacons_scanner", 10202),
    BEACONS_TAB_OPENING_ERROR_409("beacons_scanner_chanel", 10401),
    BEACONS_TAB_OPENING_ERROR_402("beacons_scanner_chanel", 10402),
    EMAIL_VERIFICATION_ERROR("beacons_scanner_chanel", 10403),
    BEACON_TAB_OPENING("beacons_scanner_chanel", 10404),
    BEACON_TAB_OPENED("beacons_scanner_chanel", 10405),
    PHONE_VERIFICATION("beacons_scanner_chanel", 10406),
    PAYMENT_METHOD_ERROR("beacons_scanner_chanel", 10407),
    TAB_CLOSED("tab_closing", 10301),
    DECLINED_PAYMENT("tab_closing", 10302),
    TICKET_NOT_FOUND_ERROR("tab_closing", 10303),
    TAB_AUTO_CLOSED("tab_closing", 10304),
    TAB_AUTO_CLOSE_TIMER("tab_closing", 10305),
    TICKET_CLOSED_EMPTY("tab_closing", 10306),
    TAB_AUTO_CLOSING_STOPPED("tab_closing", 10307),
  //CUSTOM_DIALOG("dialog_notification", 10000);
    POS_ERROR_MESSAGE("default_notification", 10101),
    RIDE_DISCOUNT_WAS_ATTACHED("ride_discount", 10501),
    AUTO_CHECK_IN_SETTINGS("profile_auto_check_in_settings", 10601),
    DEF("default_notification", 10101);

    private String chanelID;
    private int notificationID;

    private NotificationType(String chanelID, int notificationID) {
        this.chanelID = chanelID;
        this.notificationID = notificationID;
    }

    public String getChanelID() {
        return chanelID;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public static NotificationType toType(String name) {
        for (NotificationType type : NotificationType.values())
            if (type.name().equalsIgnoreCase(name))
                return type;
        return DEF;
    }
}
