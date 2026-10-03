package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum ActionType {
    BUILD("Build"),
    DESTROY("Destroy"),
    SWITCH("Switch"),
    ITEM_USE("Item Use");

    private final String commonName;

    ActionType(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
