package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.state.TaxType;

/** Provides validated rent prices and tax calculations from the server configuration. */
public final class RentManager {

    private RentManager() {}

    public static double getMinimumPrice() {
        return getNonNegativeFiniteValue(Quarters.getInstance().config().quarters.rentSettings.minPrice, 0);
    }

    public static double getMaximumPrice() {
        double configuredMaximum = getNonNegativeFiniteValue(Quarters.getInstance().config().quarters.rentSettings.maxPrice, Double.MAX_VALUE);
        return Math.max(getMinimumPrice(), configuredMaximum);
    }

    public static boolean isPriceAllowed(double price) {
        return Double.isFinite(price) && price >= getMinimumPrice() && price <= getMaximumPrice();
    }

    /**
     * Calculates the amount charged in addition to rent. This amount is deliberately not paid to the rent collector.
     */
    public static double getTax(double rent) {
        if (!Double.isFinite(rent) || rent <= 0) return 0;

        ConfigManager.RentSettings settings = Quarters.getInstance().config().quarters.rentSettings;
        double configuredTax = getNonNegativeFiniteValue(settings.tax, 0);
        double tax = settings.taxType == TaxType.PERCENTAGE ? rent * configuredTax / 100 : configuredTax;
        if (!Double.isFinite(tax)) tax = Double.MAX_VALUE;

        double minimumTax = getNonNegativeFiniteValue(settings.minTax, 0);
        double maximumTax = Math.max(minimumTax, getNonNegativeFiniteValue(settings.maxTax, Double.MAX_VALUE));
        return Math.clamp(tax, minimumTax, maximumTax);
    }

    private static double getNonNegativeFiniteValue(double value, double fallback) {
        if (!Double.isFinite(value)) return fallback;
        return Math.max(value, 0);
    }
}
