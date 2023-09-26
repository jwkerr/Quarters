package net.earthmc.quarters.utils;

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

    public static String serializePlayerList(List<Player> playerList) {
        StringBuilder sb = new StringBuilder();
        if (!playerList.isEmpty()) {
            for (Player player : playerList) {
                if (sb.length() > 0)
                    sb.append("+");

                String trustedPlayerUUID = player.getUniqueId().toString();

                sb.append(trustedPlayerUUID);
            }
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

            Player owner = null;
            if (!quarterSplit[3].equals("null")) {
                owner = Bukkit.getPlayer(quarterSplit[3]);
            }

            List<Player> trustedPlayers = new ArrayList<>();
            if (!quarterSplit[4].equals("null")) {
                final String[] trustedPlayersSplit = quarterSplit[4].split("\\+");

                for (String player : trustedPlayersSplit) {
                    trustedPlayers.add(Bukkit.getPlayer(player));
                }
            }

            Quarter quarter = new Quarter();
            quarter.setPos1(pos1);
            quarter.setPos2(pos2);
            quarter.setUUID(uuid);
            quarter.setOwner(owner);
            quarter.setTrustedPlayers(trustedPlayers);
            quarterList.add(quarter);
        }

        return quarterList;
    }

    public static List<Player> getPlayerListFromString(String string) {
        String[] split = string.split(",");

        List<Player> playerList = new ArrayList<>();
        for (String player : split) {
            playerList.add(Bukkit.getPlayer(player));
        }

        return playerList;
    }
}
