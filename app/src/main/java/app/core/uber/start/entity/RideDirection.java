package app.core.uber.start.entity;



public enum RideDirection {
    TO_BAR("to_bar"),
    FROM_BAR("from_bar");

    private final String text;

    private RideDirection(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
