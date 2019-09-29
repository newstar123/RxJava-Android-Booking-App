package app.delivering.component.push.status;

public enum PushNotificationStatus {
    RIDE_TO_VENUE_COMPLETED("RIDE_TO_VENUE_COMPLETED"),
    RIDE_FROM_VENUE_COMPLETED("RIDE_FROM_VENUE_COMPLETED"),
    TAB_TICKET_UPDATED("TAB_TICKET_UPDATED"),
    TAB_TICKET_CLOSED("TAB_TICKET_CLOSED"),
    PRE_AUTH_FUNDS_ERROR("PRE_AUTH_FUNDS_ERROR"),
    TAB_TICKET_CLOSED_EMPTY("TAB_TICKET_CLOSED_EMPTY"),
    CURRENT_POS_ERROR("CURRENT_POS_ERROR"),
    POS_ERROR_CHECKIN("POS_ERROR_CHECKIN"),
    POS_ERROR_CLOSE_CHECKIN("POS_ERROR_CLOSE_CHECKIN"),
    POS_ERROR_WITHOUT_CHECKIN("POS_ERROR_WITHOUT_CHECKIN"),
    DEFAULT("");

    private String status;

    private PushNotificationStatus(String status) {
        this.status = status;
    }

    public static PushNotificationStatus toType(String name) {
        for (PushNotificationStatus type : PushNotificationStatus.values())
            if (type.status.equalsIgnoreCase(name))
                return type;
        return DEFAULT;
    }
}
