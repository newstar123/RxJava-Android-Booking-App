package app.delivering.mvp.bars.list.init.enums;

public enum BarType {
    MUSIC("music venue", "#C48037"),
    ROOFTOP("rooftop", "#DA9C59"),
    SPORTS("sports bar", "#CCAC3B"),
    PUB("pub", "#B3CB52"),
    NEIGHBORHOOD("neighborhood bar", "#68BE68"),
    SPEAKEASY("speakeasy", "#34B5A3"),
    BEER("beer garden", "#00ABDD"),
    COCTAIL("cocktail bar", "#405DDE"),
    GASTROPUB("gastropub", "#725EE7"),
    WINE("wine bar", "#B473E6"),
    NIGHTCLUB("nightclub", "#B048AC"),
    LOUNGE("lounge", "#902A8C"),
    DEF("default bar", "#1bacdb");

    private String name;
    private String color;

    private BarType(String nameValue, String colorValue) {
        name = nameValue;
        color = colorValue;
    }

    public static BarType toType(String name) {
        for (BarType type : BarType.values())
            if (type.name.equalsIgnoreCase(name))
                return type;
        return DEF;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
