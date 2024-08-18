package au.lupine.quarters.object.state;

public enum QuarterType {
    APARTMENT("Apartment"), // Default type
    INN("Inn"), // Allows bed usage
    STATION("Station"); // Allows vehicle placing and usage

    private final String commonName;

    QuarterType(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getLowerCase() {
        return commonName.toLowerCase();
    }
}
