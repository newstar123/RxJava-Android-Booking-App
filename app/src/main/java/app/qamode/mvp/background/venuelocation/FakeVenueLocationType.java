package app.qamode.mvp.background.venuelocation;

public enum FakeVenueLocationType {
    CUSTOM(),
    CURRENT();

    public static FakeVenueLocationType toType(String name) {
        for (FakeVenueLocationType type : FakeVenueLocationType.values())
            if (type.name().equalsIgnoreCase(name))
                return type;
        return CURRENT;
    }
}
