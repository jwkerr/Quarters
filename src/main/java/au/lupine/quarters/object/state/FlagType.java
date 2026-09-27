package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum FlagType {
    PVP("PVP"),
    MOBS("Mobs"),
    EXPLOSIONS("Explosions");

    private final String commonName;

    FlagType(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
