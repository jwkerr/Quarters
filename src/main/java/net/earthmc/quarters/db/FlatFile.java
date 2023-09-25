package net.earthmc.quarters.db;

import com.palmergames.bukkit.towny.object.TownBlock;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.DataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.utils.QuarterUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class FlatFile {
    static File file = new File(Quarters.instance.getDataFolder(), "data");

    public static void init() {
        File quartersFile = new File(file, "quarters");

        if (!file.exists()) {
            if (!file.mkdir()) {
                Quarters.instance.getLogger().severe("Failed to create data file");
            }
        }

        if (!quartersFile.exists()) {
            if (!quartersFile.mkdir()) {
                Quarters.instance.getLogger().severe("Failed to create quarters file");
            }
        }

        loadQuarters();
    }

    private static void loadQuarters() {
        File quartersFile = new File(file, "quarters");
        File[] files = quartersFile.listFiles();

        if (files == null)
            return;

        for (File file : files) {
            try {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    FileInputStream input = new FileInputStream(file);
                    Properties properties = new Properties();

                    properties.load(input);

                    TownBlock townBlock = QuarterUtils.getTownBlockFromString(properties.getProperty("townBlock"));
                    List<Quarter> quarterList = DataManager.quarterMap.computeIfAbsent(townBlock, k -> new ArrayList<>());

                    Quarter quarter = new Quarter();
                    setQuarterProperties(quarter, properties);

                    quarterList.add(quarter);
                }
            } catch (IOException e) {
                Quarters.instance.getLogger().severe("Failed to load quarter files" + "\n" + e);
            }
        }
    }

    public static void loadQuarter(UUID uuid) {
        File quartersFile = new File(file, "quarters");
        File[] files = quartersFile.listFiles();

        if (files == null)
            return;

        for (File file : files) {
            try {
                if (file.getName().equals(uuid.toString() + ".txt")) {
                    FileInputStream input = new FileInputStream(file);
                    Properties properties = new Properties();

                    properties.load(input);

                    TownBlock townBlock = QuarterUtils.getTownBlockFromString(properties.getProperty("townBlock"));
                    List<Quarter> quarterList = DataManager.quarterMap.computeIfAbsent(townBlock, k -> new ArrayList<>());

                    for (Quarter quarter : quarterList) {
                        if (quarter.getUuid() == uuid) {
                            setQuarterProperties(quarter, properties);
                            return;
                        }
                    }

                    Quarter quarter = new Quarter();
                    setQuarterProperties(quarter, properties);
                    quarterList.add(quarter);
                }
            } catch (IOException e) {
                Quarters.instance.getLogger().severe("Failed to reload quarter file " + uuid + "\n" + e);
            }
        }
    }

    private static void setQuarterProperties(Quarter quarter, Properties properties) {
        quarter.setUuid(UUID.fromString(properties.getProperty("uuid")));
        quarter.setPos1(QuarterUtils.getLocationFromString(properties.getProperty("pos1")));
        quarter.setPos2(QuarterUtils.getLocationFromString(properties.getProperty("pos2")));
        quarter.setTownBlock(QuarterUtils.getTownBlockFromString(properties.getProperty("townBlock")));
        quarter.setOwner(properties.getProperty("owner").isEmpty() ? null : Bukkit.getPlayer(properties.getProperty("owner")));
        quarter.setTrustedPlayers(properties.getProperty("trustedPlayers").isEmpty() ? null : QuarterUtils.getPlayerListFromString(properties.getProperty("trustedPlayers")));
    }

    public static void createQuarter(Location pos1, Location pos2, TownBlock townBlock) {
        UUID uuid = UUID.randomUUID();

        Properties properties = new Properties();
        properties.setProperty("uuid", uuid.toString());
        properties.setProperty("pos1", QuarterUtils.serializeLocation(pos1));
        properties.setProperty("pos2", QuarterUtils.serializeLocation(pos2));
        properties.setProperty("town", townBlock.getTownOrNull().getUUID().toString());
        properties.setProperty("townBlock", QuarterUtils.serializeTownBlock(townBlock));
        properties.setProperty("owner", "");
        properties.setProperty("trustedPlayers", "");

        try (FileOutputStream fos = new FileOutputStream(new File(file, "quarters/" + uuid + ".txt"))) {
            properties.store(fos, uuid.toString());
            loadQuarter(uuid);
        } catch (IOException e) {
            Quarters.instance.getLogger().severe("Failed to create quarter in " + townBlock.getTownOrNull().getName() + "\n" + e);
        }
    }
}
