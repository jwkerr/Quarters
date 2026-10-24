package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum PermLevel {
    RESIDENT("Resident"),
    NATION("Nation"),
    ALLY("Ally"),
    OUTSIDER("Outsider");

    private final String commonName;

    PermLevel(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
