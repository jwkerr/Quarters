package au.lupine.quarters.object.state;

public enum ActionType {
    BUILD("Build"),
    DESTROY("Destroy"),
    SWITCH("Switch"),
    ITEM_USE("ItemUse");

    private final String commonName;

    ActionType(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getLowerCase() {
        return commonName.toLowerCase();
    }
}
