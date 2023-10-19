package net.earthmc.quarters.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuarterUtil {
    public static List<Cuboid> getCuboidsFromString(String string) {
        List<Cuboid> cuboids = new ArrayList<>();

        String[] split = string.split(":");
        for (String cuboidString : split) {
            String[] locationSplit = cuboidString.split(";");

            Location pos1 = getLocationFromString(locationSplit[0]);
            Location pos2 = getLocationFromString(locationSplit[1]);

            cuboids.add(new Cuboid(pos1, pos2));
        }

        return cuboids;
    }

    public static String serializeCuboids(List<Cuboid> cuboids) {
        StringBuilder sb = new StringBuilder();
        for (Cuboid cuboid : cuboids) {
            if (sb.length() > 0)
                sb.append(":");

            sb.append(serializeLocation(cuboid.getPos1()));
            sb.append(";");
            sb.append(serializeLocation(cuboid.getPos2()));
        }

        return sb.toString();
    }

    private static Location getLocationFromString(String string) {
        String[] split = string.split("\\+");

        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    private static String serializeLocation(Location location) {
        return location.getWorld().getName() + "+" + location.getBlockX() + "+" + location.getBlockY() + "+" + location.getBlockZ();
    }

    public static List<UUID> getUUIDListFromString(String string) {
        String[] split = string.split("\\+");

        List<UUID> uuidList = new ArrayList<>();
        for (String uuid : split) {
            uuidList.add(UUID.fromString(uuid));
        }

        return uuidList;
    }

    public static Double getDoubleFromString(String string) {
        if (string.equals("null"))
            return null;

        return Double.parseDouble(string);
    }

    public static String serializeUUIDList(List<UUID> uuidList) {
        if (uuidList == null || uuidList.isEmpty())
            return "null";

        StringBuilder sb = new StringBuilder();
        for (UUID uuidFromList : uuidList) {
            if (sb.length() > 0)
                sb.append("+");

            String uuid = uuidFromList.toString();

            sb.append(uuid);
        }

        return sb.toString();
    }

    public static List<Quarter> deserializeQuarterListString(String string) {
        final String[] quarterListSplit = string.split("\\|");
        List<Quarter> quarterList = new ArrayList<>();

        for (String quarterString : quarterListSplit) {
            final String[] quarterSplit = quarterString.split(",");

            List<Cuboid> cuboids = getCuboidsFromString(quarterSplit[0]);
            UUID uuid = UUID.fromString(quarterSplit[1]);
            UUID town = UUID.fromString(quarterSplit[2]);

            UUID ownerUUID = null;
            if (!quarterSplit[3].equals("null"))
                ownerUUID = UUID.fromString(quarterSplit[3]);

            List<UUID> trustedResidentsUUIDs = new ArrayList<>();
            if (!quarterSplit[4].equals("null"))
                trustedResidentsUUIDs = getUUIDListFromString(quarterSplit[4]);

            Double price = getDoubleFromString(quarterSplit[5]);
            QuarterType type = QuarterType.getByName(quarterSplit[6]);
            boolean isEmbassy = Boolean.parseBoolean(quarterSplit[7]);
            Long registered = Long.parseLong(quarterSplit[8]);

            Long claimedAt = null;
            if (!quarterSplit[9].equals("null"))
                claimedAt = Long.parseLong(quarterSplit[9]);

            final String[] rgbSplit = quarterSplit[10].split("\\+");
            int r = Integer.parseInt(rgbSplit[0]);
            int g = Integer.parseInt(rgbSplit[1]);
            int b = Integer.parseInt(rgbSplit[2]);
            int[] rgb = new int[]{r, g, b};

            Quarter quarter = new Quarter();
            quarter.setCuboids(cuboids);
            quarter.setUUID(uuid);
            quarter.setTown(town);
            quarter.setOwner(ownerUUID);
            quarter.setTrusted(trustedResidentsUUIDs);
            quarter.setPrice(price);
            quarter.setType(type);
            quarter.setEmbassy(isEmbassy);
            quarter.setRegistered(registered);
            quarter.setClaimedAt(claimedAt);
            quarter.setRGB(rgb);
            quarterList.add(quarter);
        }

        return quarterList;
    }

    public static boolean shouldRenderOutlines(QuartersPlayer quartersPlayer, Material itemHeld) {
        if (quartersPlayer.hasConstantOutlines() && Quarters.INSTANCE.getConfig().getBoolean("particles.allow_constant_outlines"))
            return true;

        return itemHeld == Quarters.WAND;
    }

    public static Quarter getQuarter(Location location) {
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null)
            return null;

        QuartersTown quartersTown = new QuartersTown(town);
        if (!quartersTown.hasQuarter())
            return null;

        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList == null)
            return null;

        for (Quarter quarter : quarterList) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (cuboid.isLocationInsideBounds(location))
                    return quarter;
            }
        }

        return null;
    }

    public static List<Quarter> getAllQuarters() {
        List<Quarter> quarterList = new ArrayList<>();

        for (Town town : TownyAPI.getInstance().getTowns()) {
            List<Quarter> currentTownQuarterList = new QuartersTown(town).getQuarters();
            if (currentTownQuarterList != null) {
                quarterList.addAll(currentTownQuarterList);
            }
        }

        return quarterList;
    }
}
