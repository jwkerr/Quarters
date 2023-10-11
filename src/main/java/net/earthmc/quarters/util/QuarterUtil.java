package net.earthmc.quarters.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuarterUtil {
    public static Location getLocationFromString(String string) {
        String[] split = string.split("\\+");

        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + "+" + location.getBlockX() + "+" + location.getBlockY() + "+" + location.getBlockZ();
    }

    public static List<Resident> getResidentListFromString(String string) {
        String[] split = string.split("\\+");

        List<Resident> residentList = new ArrayList<>();
        for (String uuid : split) {
            residentList.add(TownyAPI.getInstance().getResident(UUID.fromString(uuid)));
        }

        return residentList;
    }

    public static Double getDoubleFromString(String string) {
        if (string.equals("null"))
            return null;

        return Double.parseDouble(string);
    }

    public static String serializeResidentList(List<Resident> residentList) {
        if (residentList == null || residentList.isEmpty())
            return "null";

        StringBuilder sb = new StringBuilder();
        for (Resident resident : residentList) {
            if (sb.length() > 0)
                sb.append("+");

            String uuid = resident.getUUID().toString();

            sb.append(uuid);
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

            Resident owner = null;
            if (!quarterSplit[4].equals("null"))
                owner = TownyAPI.getInstance().getResident(quarterSplit[4]);

            List<Resident> trustedPlayers = new ArrayList<>();
            if (!quarterSplit[5].equals("null"))
                trustedPlayers = getResidentListFromString(quarterSplit[5]);

            Double price = getDoubleFromString(quarterSplit[6]);

            QuarterType type = QuarterType.valueOf(quarterSplit[7]);

            Quarter quarter = new Quarter();
            quarter.setPos1(pos1);
            quarter.setPos2(pos2);
            quarter.setUUID(uuid);
            quarter.setTown(town);
            quarter.setOwner(owner);
            quarter.setTrustedResidents(trustedPlayers);
            quarter.setPrice(price);
            quarter.setType(type);
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
