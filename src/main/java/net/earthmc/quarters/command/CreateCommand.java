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
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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
            QuartersMessaging.sendErrorMessage(player, "您没有圈选任何区域，通过 /quarters selection add 来将全选的区域加入区域列表");
            return;
        }

        Town town = TownyAPI.getInstance().getTown(cuboids.get(0).getPos1());
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "Could not resolve a town from the first selected position");
            return;
        }

        if (!isCuboidListInValidLocation(cuboids)) {
            QuartersMessaging.sendErrorMessage(player, "Selected quarter is not in a valid location");
            return;
        }

        int maxQuarters = Quarters.INSTANCE.getConfig().getInt("quarters.max_quarters_per_town");
        if (maxQuarters > 0) {
            QuartersTown quartersTown = new QuartersTown(town);
            List<Quarter> quarterList = quartersTown.getQuarters();
            if (quarterList != null && quarterList.size() == maxQuarters) {
                QuartersMessaging.sendErrorMessage(player, "Selected quarter could not be created as " + town.getName() + " will exceed the configured quarter limit of " + maxQuarters);
                return;
            }
        }

        Quarter quarter = new Quarter();
        quarter.setCuboids(new ArrayList<>(cuboids));
        quarter.setUUID(UUID.randomUUID());
        quarter.setTown(town.getUUID());
        quarter.setOwner(null);
        quarter.setTrusted(new ArrayList<>());
        quarter.setPrice(null);
        quarter.setType(QuarterType.APARTMENT);
        quarter.setEmbassy(false);
        quarter.setRegistered(Instant.now().toEpochMilli());
        quarter.setClaimedAt(null);
        quarter.setRGB(getRGBValue());


        int maxVolume = Quarters.INSTANCE.getConfig().getInt("quarters.max_quarter_volume");
        if (maxVolume > 0) {
            if (quarter.getVolume() > maxVolume) {
                QuartersMessaging.sendErrorMessage(player, "This quarter is too large, the max configured volume is " + maxVolume + " blocks");
                return;
            }
        }

        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList == null) {
            quarterList = new ArrayList<>();
        }
        quarterList.add(quarter);

        TownMetadataManager.setQuarterListOfTown(town, quarterList);

        Location location = cuboids.get(0).getPos1();
        cuboids.clear();
        SellCommand.setQuarterForSale(player,quarter,Quarters.DEFAULT_PRICE);

        QuartersMessaging.sendSuccessMessage(player, "公寓区已成功创建");
        QuartersMessaging.sendInfoMessageToTown(town, player, player.getName() + " 创建了一个公寓 " + QuartersMessaging.getLocationString(location));
    }

    private boolean isCuboidListInValidLocation(List<Cuboid> cuboids) {
        for (Cuboid cuboid : cuboids) {
            if (!cuboid.isInValidLocation())
                return false;
        }

        return true;
    }

    private int[] getRGBValue() {
        int r;
        int g;
        int b;

        FileConfiguration config = Quarters.INSTANCE.getConfig();
        if (config.getBoolean("quarters.default_colour.enabled")) {
            r = config.getInt("quarters.default_colour.red");
            g = config.getInt("quarters.default_colour.green");
            b = config.getInt("quarters.default_colour.blue");
        } else {
            Random random = new Random();
            r = random.nextInt(256);
            g = random.nextInt(256);
            b = random.nextInt(256);
        }

        return new int[]{r, g, b};
    }
}
