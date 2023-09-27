package net.earthmc.quarters.utils;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuarterUtils {
    public static Location getLocationFromString(String string) {
        String[] split = string.split("\\+");

        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + "+" + location.getBlockX() + "+" + location.getBlockY() + "+" + location.getBlockZ();
    }

    public static List<Player> getPlayerListFromString(String string) {
        String[] split = string.split("\\+");

        List<Player> playerList = new ArrayList<>();
        for (String player : split) {
            playerList.add(Bukkit.getPlayer(player));
        }

        return playerList;
    }

    public static String serializePlayerList(List<Player> playerList) {
        if (playerList == null || playerList.isEmpty())
            return "null";

        StringBuilder sb = new StringBuilder();
        for (Player player : playerList) {
            if (sb.length() > 0)
                sb.append("+");

            String trustedPlayerUUID = player.getUniqueId().toString();

            sb.append(trustedPlayerUUID);
        }

        return sb.toString();
    }

    public static List<Quarter> deserializeQuarterListString(String string) {
        final String[] quarterListSplit = string.split("\\|");
        List<Quarter> quarterList = new ArrayList<>();

        for (String quarterString : quarterListSplit) {
            final String[] quarterSplit = quarterString.split(",");

            Location pos1 = getLocationFromString(quarterSplit[0]);
            Location pos2 = getLocationFromString(quarterSplit[1]);
            UUID uuid = UUID.fromString(quarterSplit[2]);
            UUID town = UUID.fromString(quarterSplit[3]);

            UUID owner = null;
            if (!quarterSplit[4].equals("null")) {
                owner = UUID.fromString(quarterSplit[4]);
            }

            List<Player> trustedPlayers = new ArrayList<>();
            if (!quarterSplit[5].equals("null")) {
                trustedPlayers = getPlayerListFromString(quarterSplit[5]);
            }

            Quarter quarter = new Quarter();
            quarter.setPos1(pos1);
            quarter.setPos2(pos2);
            quarter.setUUID(uuid);
            quarter.setTown(town);
            quarter.setOwner(owner);
            quarter.setTrustedPlayers(trustedPlayers);
            quarterList.add(quarter);
        }

        return quarterList;
    }

    public static boolean isLocationInsideCuboidBounds(Location location, Cuboid cuboid) {
        if (location.getY() < cuboid.getMinY() || location.getY() > cuboid.getMaxY())
            return false;

        if (location.getX() < cuboid.getMinX() || location.getX() > cuboid.getMaxX())
            return false;

        if (location.getZ() < cuboid.getMinZ() || location.getZ() > cuboid.getMaxZ())
            return false;

        return true;
    }
}
