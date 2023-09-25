package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.earthmc.quarters.manager.DataManager;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class QuartersAPI {
    static TownyAPI townyAPI = TownyAPI.getInstance();

    public static boolean hasQuarter(TownBlock townBlock) {
        return DataManager.quarterMap.get(townBlock) != null;
    }

    public static boolean isPlayerInQuarter(Player player) {
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

    public static Quarter getQuarter(Location location) {
        TownBlock townBlock = townyAPI.getTownBlock(location);
        if (!hasQuarter(townBlock))
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
}
