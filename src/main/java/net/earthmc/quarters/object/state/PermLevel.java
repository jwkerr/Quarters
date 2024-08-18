package net.earthmc.quarters.object.state;

public enum PermLevel {
    RESIDENT("Resident"),
    NATION("Nation"),
    ALLY("Ally"),
    OUTSIDER("Outsider");

    private final String commonName;

    PermLevel(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getLowerCase() {
        return commonName.toLowerCase();
    }
}
