package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.DataManager;
import net.earthmc.quarters.object.Quarter;
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
        return DataManager.quarterMap.get(townBlock) != null;
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

        for (Quarter quarter : DataManager.quarterMap.get(townBlock)) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
            int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
            int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
            int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            Location playerLocation = player.getLocation();
            if (playerLocation.getY() < minY | playerLocation.getY() > maxY)
                return false;

            if (playerLocation.getX() < minX | playerLocation.getX() > maxX)
                return false;

            if (playerLocation.getZ() < minZ | playerLocation.getZ() > maxZ)
                return false;

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
    public Quarter getQuarter(Location location) {
        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (hasQuarter(townBlock))
            return null;

        for (Quarter quarter : DataManager.quarterMap.get(townBlock)) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
            int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
            int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
            int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            if (location.getY() < minY | location.getY() > maxY)
                return null;

            if (location.getX() < minX | location.getX() > maxX)
                return null;

            if (location.getZ() < minZ | location.getZ() > maxZ)
                return null;

            return quarter;
        }

        return null;
    }

    /**
     * Gets all the quarters within a TownBlock
     *
     * @param townBlock TownBlock to check
     * @return A list of all quarters within the specified TownBlock
     */
    public List<Quarter> getQuarters(TownBlock townBlock) {
        return DataManager.quarterMap.get(townBlock);
    }
}
