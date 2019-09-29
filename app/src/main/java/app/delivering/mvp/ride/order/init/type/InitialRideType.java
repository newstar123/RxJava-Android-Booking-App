package app.delivering.mvp.ride.order.init.type;

public enum InitialRideType {
    TO_THE_VENUE(1),
    FROM_THE_VENUE(2),
    CUSTOM(3);

    private int type;

    private InitialRideType(int type) {
        this.type = type;
    }

    public static InitialRideType toType(int typeValue) {
        for (InitialRideType type : InitialRideType.values())
            if (type.type == typeValue)
                return type;
        return TO_THE_VENUE;
    }

    public int getValue() {
        return type;
    }
}
