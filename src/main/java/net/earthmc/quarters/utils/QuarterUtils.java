package net.earthmc.quarters.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuarterUtils {
    public static TownBlock getTownBlockFromString(String string) {
        String[] split = string.split(",");

        WorldCoord worldCoord = new WorldCoord(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));

        return TownyAPI.getInstance().getTownBlock(worldCoord);
    }

    public static Location getLocationFromString(String string) {
        String[] split = string.split(",");

        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public static List<Player> getPlayerListFromString(String string) {
        String[] split = string.split(",");

        List<Player> playerList = new ArrayList<>();
        for (String player : split) {
            playerList.add(Bukkit.getPlayer(player));
        }

        return playerList;
    }

    public static String serializeTownBlock(TownBlock townBlock) {
        return townBlock.getWorld() + "," + townBlock.getX() + "," + townBlock.getZ();
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}
