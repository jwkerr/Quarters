package net.earthmc.quarters.object;

public enum QuarterType {
    APARTMENT("apartment"),
    COMMONS("commons"),
    PUBLIC("public"),
    SHOP("shop"),
    STATION("station"),
    WORKSITE("worksite");

    private final String name;

    QuarterType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getFormattedName() {
        return this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
    }

    public static QuarterType getByName(String string) {
        for (QuarterType quarterType : values()) {
            if (quarterType.name.equals(string)) {
                return quarterType;
            }
        }

        return null;
    }


}
