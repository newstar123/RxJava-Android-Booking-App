package app.delivering.mvp.bars.list.init.enums;

public enum BarByWorkTime {
    OPEN(0, "#D0021B"),
    CLOSES_SOON(1, "#dd8700"),
    CLOSED(2, "#dd0000");

    private int value;
    private String color;

    private BarByWorkTime(int value, String color) {
        this.value = value;
        this.color = color;
    }

    public static BarByWorkTime toType(int value) {
        for (BarByWorkTime timeType : BarByWorkTime.values())
            if (timeType.getValue() == value)
                return timeType;
        return OPEN;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
