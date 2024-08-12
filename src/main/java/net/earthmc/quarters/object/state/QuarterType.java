package net.earthmc.quarters.object.state;

public enum QuarterType {
    APARTMENT("Apartment"),
    COMMONS("Commons"),
    PUBLIC("Public"),
    SHOP("Shop"),
    STATION("Station"),
    WORKSITE("Worksite"); // TODO: reconsider which of these are necessary after perm rewrite

    private final String commonName;

    QuarterType(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }
}
