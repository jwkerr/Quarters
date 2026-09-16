package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum QuarterType {
    APARTMENT("Apartment"), // Default type
    INN("Inn"), // Allows bed usage
    STATION("Station"), // Allows vehicle placing and usage
    ARENA("Arena"); // Allows PvP

    private final String commonName;

    QuarterType(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
