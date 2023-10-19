package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.TownMetadataManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuartersTown {
    private final Town town;
    private final boolean shouldSellOnDelete;

    public QuartersTown(Town town) {
        this.town = town;
        this.shouldSellOnDelete = TownMetadataManager.shouldSellOnDelete(town);
    }

    /**
     *
     * @return The town's Towny town instance
     */
    public Town getTown() {
        return this.town;
    }

    /**
     * Gets a boolean representing whether there is at least one quarter in the town
     *
     * @return True if there is at least one quarter defined within the town
     */
    public boolean hasQuarter() {
        return getQuarters() != null;
    }

    /**
     * Gets a list of all the quarters in the town
     *
     * @return A list of quarters or null if there are none
     */
    @Nullable
    public List<Quarter> getQuarters() {
        return TownMetadataManager.getQuarterListOfTown(town);
    }

    /**
     * Gets the towns configured default sell price
     * This determines the sale price of quarters when using /q sell with no number specified
     *
     * @return A Double representing the default sell price, null if it is not configured
     */
    @Nullable
    public Double getDefaultSellPrice() {
        return TownMetadataManager.getDefaultSellPriceOfTown(town);
    }

    /**
     * Gets a boolean representing whether quarters in this town should automatically go for sale when the owner is deleted
     *
     * @return True if quarters should go for sale on resident delete
     */
    public boolean shouldSellOnDelete() {
        return this.shouldSellOnDelete;
    }
}
