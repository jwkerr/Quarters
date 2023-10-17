package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.TownMetadataManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuartersTown {
    private final Town town;

    public QuartersTown(Town town) {
        this.town = town;
    }

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

    public Double getDefaultSellPrice() {
        return TownMetadataManager.getDefaultSellPriceOfTown(town);
    }
}
