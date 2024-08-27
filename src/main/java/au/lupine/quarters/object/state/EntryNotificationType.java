package au.lupine.quarters.object.state;

public enum EntryNotificationType {
    ACTION_BAR("Action Bar"),
    CHAT("Chat");

    private final String commonName;

    EntryNotificationType(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getLowerCase() {
        return name().toLowerCase();
    }
}
