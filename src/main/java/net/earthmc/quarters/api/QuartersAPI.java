package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.utils.QuarterUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
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
        return Quarters.WAND;
    }

    /**
     * Gets a boolean representing whether there is at least one quarter in the specified town
     *
     * @param town Town to check
     * @return True if there is at least one quarter defined within the town
     */
    public boolean hasQuarter(Town town) {
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);

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
        Town town = townyAPI.getTown(location);
        if (town == null)
            return false;

        if (!hasQuarter(town))
            return false;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);

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
    @Nullable
    public Quarter getQuarter(Location location) {
        Town town = townyAPI.getTown(location);
        if (town == null)
            return null;

        if (!hasQuarter(town))
            return null;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);
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
     * Gets all the quarters within a town
     *
     * @param town Town to check
     * @return A list of all quarters within the specified town or null if there are none
     */
    public List<Quarter> getQuartersInTown(Town town) {
        return QuarterDataManager.getQuarterListFromTown(town);
    }
}
