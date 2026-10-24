package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum RentCollector {
    TOWN("Town"),
    MAYOR("Mayor"),
    NATION("Nation"),
    LEADER("Leader"),
    SERVER("Server");

    private final String commonName;

    RentCollector(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
