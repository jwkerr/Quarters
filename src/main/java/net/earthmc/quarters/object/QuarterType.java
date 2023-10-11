package net.earthmc.quarters.object;

public enum QuarterType {
    APARTMENT("apartment"),
    SHOP("shop"),
    STATION("station");

    private final String name;

    QuarterType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
