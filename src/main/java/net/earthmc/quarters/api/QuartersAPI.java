package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.utils.QuarterUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class QuartersAPI {
    private static QuartersAPI instance;
    static TownyAPI townyAPI = TownyAPI.getInstance();

    public static QuartersAPI getInstance() {
        if (instance == null)
            instance = new QuartersAPI();

        return instance;
    }

    /**
     * Gets the wand item's material
     *
     * @return Material of the configured Quarters wand
     */
    public Material getWand() {
        return Quarters.wand;
    }

    /**
     * Gets a boolean representing whether there is at least one quarter in the specified TownBlock
     *
     * @param townBlock TownBlock to check
     * @return True if there is at least one quarter defined within the TownBlock
     */
    public boolean hasQuarter(TownBlock townBlock) {
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTownBlock(townBlock);

        return quarterList != null && !quarterList.isEmpty();
    }

    /**
     * Gets a boolean representing if the specified player is inside a quarter or not
     *
     * @param player Player to check
     * @return True if the player is inside a quarter
     */
    public boolean isPlayerInQuarter(Player player) {
        Location location = player.getLocation();
        if (townyAPI.getTown(location) == null)
            return false;

        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (!hasQuarter(townBlock))
            return false;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTownBlock(townBlock);

        if (quarterList == null)
            return false;

        for (Quarter quarter : quarterList) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            Location playerLocation = player.getLocation();
            if (QuarterUtils.isLocationInsideCuboidBounds(playerLocation, new Cuboid(pos1, pos2)))
                return true;
        }

        return false;
    }

    /**
     * Gets a quarter at a specific location
     *
     * @param location Location to check
     * @return The quarter at the specified location or null if there is none
     */
    public Quarter getQuarterAtLocation(Location location) {
        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (!hasQuarter(townBlock))
            return null;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTownBlock(townBlock);

        if (quarterList == null)
            return null;

        for (Quarter quarter : quarterList) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            if (QuarterUtils.isLocationInsideCuboidBounds(location, new Cuboid(pos1, pos2)))
                return quarter;
        }

        return null;
    }

    /**
     * Gets all the quarters within a TownBlock
     *
     * @param townBlock TownBlock to check
     * @return A list of all quarters within the specified TownBlock or null if there are none
     */
    public List<Quarter> getQuartersInTownBlock(TownBlock townBlock) {
        return QuarterDataManager.getQuarterListFromTownBlock(townBlock);
    }
}
