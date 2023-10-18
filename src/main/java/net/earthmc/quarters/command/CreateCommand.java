package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.manager.SelectionManager;
import net.earthmc.quarters.object.QuartersTown;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@CommandAlias("quarters|q")
public class CreateCommand extends BaseCommand {
    @Subcommand("create")
    @Description("Turn selected area into a quarter")
    @CommandPermission("quarters.command.quarters.create")
    public void onCreate(Player player) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.create"))
            return;

        List<Cuboid> cuboids = SelectionManager.cuboidsMap.computeIfAbsent(player, k -> new ArrayList<>());
        if (cuboids.isEmpty()) {
            QuartersMessaging.sendErrorMessage(player, "You have not selected any areas, add your current selection with /quarters selection add");
            return;
        }

        Town town = TownyAPI.getInstance().getTown(cuboids.get(0).getPos1());
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "Could not resolve a town from the first selected position");
            return;
        }

        if (!isCuboidListInValidLocation(cuboids, town)) {
            QuartersMessaging.sendErrorMessage(player, "Selected quarter is not in a valid location");
            return;
        }

        Quarter quarter = new Quarter();
        quarter.setCuboids(new ArrayList<>(cuboids));
        quarter.setUUID(UUID.randomUUID());
        quarter.setTown(town.getUUID());
        quarter.setOwnerUUID(null);
        quarter.setTrustedResidentsUUIDs(new ArrayList<>());
        quarter.setPrice(null);
        quarter.setType(QuarterType.APARTMENT);
        quarter.setEmbassy(false);
        quarter.setRegistered(Instant.now().toEpochMilli());
        quarter.setClaimedAt(null);
        quarter.setRGB(getRandomRGB());

        int maxVolume = Quarters.INSTANCE.getConfig().getInt("max_quarter_volume");
        if (quarter.getVolume() > maxVolume) {
            QuartersMessaging.sendErrorMessage(player, "This quarter is too large, the max configured volume is " + maxVolume + " blocks");
            return;
        }

        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList == null) {
            quarterList = new ArrayList<>();
        }
        quarterList.add(quarter);

        TownMetadataManager.setQuarterListOfTown(town, quarterList);

        cuboids.clear();

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");
    }

    private boolean isCuboidListInValidLocation(List<Cuboid> cuboids, Town town) {
        QuartersTown quartersTown = new QuartersTown(town);
        List<Quarter> quarterList = quartersTown.getQuarters();

        for (Cuboid cuboid : cuboids) {
            if (!cuboid.isEntirelyWithinTown()) {
                return false;
            }

            if (quarterList != null) {
                if (cuboid.isIntersectingPreexistingQuarter()) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] getRandomRGB() {
        Random random = new Random();

        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return new int[]{r, g, b};
    }
}
