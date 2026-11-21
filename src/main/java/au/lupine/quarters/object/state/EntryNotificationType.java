package au.lupine.quarters.object.state;

import org.jetbrains.annotations.NotNull;

public enum EntryNotificationType {
    ACTION_BAR("Action Bar"),
    CHAT("Chat");

    private final String commonName;

    EntryNotificationType(@NotNull String commonName) {
        this.commonName = commonName;
    }

    public @NotNull String getCommonName() {
        return commonName;
    }

    public @NotNull String getLowerCase() {
        return name().toLowerCase();
    }
}
