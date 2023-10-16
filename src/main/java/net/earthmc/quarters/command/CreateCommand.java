package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.manager.SelectionManager;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("quarters|q")
public class CreateCommand extends BaseCommand {
    @Subcommand("create")
    @Description("Turn selected area into a quarter")
    @CommandPermission("quarters.command.quarters.create")
    public void onCreate(Player player) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.create"))
            return;

        Selection selection = SelectionManager.selectionMap.get(player);
        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();

        List<Cuboid> cuboids = SelectionManager.cuboidsMap.get(player);
        if (cuboids.isEmpty()) {
            if (pos1 != null && pos2 != null) {
                cuboids.add(new Cuboid(pos1, pos2));
            } else {
                QuartersMessaging.sendErrorMessage(player, "You have not selected any areas");
            }
        }

        Town town = TownyAPI.getInstance().getTown(selection.getPos1());
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "Could not resolve a town from the first selected position");
            return;
        }

        if (!isCuboidListInValidLocation(cuboids, town)) { // TODO: add a "why?" message (check was generalised to allow for simpler reuse of valid location check)
            QuartersMessaging.sendErrorMessage(player, "Selected quarter is not in a valid location");
            return;
        }

        selection.setPos1(null);
        selection.setPos2(null);

        Quarter newQuarter = new Quarter();
        newQuarter.setCuboids(cuboids);
        newQuarter.setUUID(UUID.randomUUID());
        newQuarter.setTown(town.getUUID());
        newQuarter.setOwner(null);
        newQuarter.setTrustedResidents(new ArrayList<>());
        newQuarter.setPrice(null);
        newQuarter.setType(QuarterType.APARTMENT);

        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList == null) {
            quarterList = new ArrayList<>();
        }
        quarterList.add(newQuarter);

        TownMetadataManager.setQuarterListOfTown(town, quarterList);

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");
    }

    private boolean isCuboidListInValidLocation(List<Cuboid> cuboids, Town town) {
        List<Quarter> quarterList = QuartersAPI.getInstance().getQuartersTown(town).getQuarters();

        for (Cuboid cuboid : cuboids) {
            if (!isCuboidEntirelyInOneTown(cuboid, town)) {
                return false;
            }

            if (quarterList != null) {
                if (!isCuboidIntersectingPreExistingQuarter(cuboid, quarterList)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isCuboidEntirelyInOneTown(Cuboid cuboid, Town town) {
        for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
            for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                    Location location = new Location(town.getWorld(), x, y, z);

                    if (TownyAPI.getInstance().getTown(location) == null)
                        return false;

                    Town currentPosTown = TownyAPI.getInstance().getTown(location);
                    if (town != currentPosTown)
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isCuboidIntersectingPreExistingQuarter(Cuboid cuboid, List<Quarter> quarterList) {
        for (Quarter quarter : quarterList) {
            for (Cuboid oldCuboid : quarter.getCuboids()) {
                if (cuboid.doesIntersectWith(oldCuboid))
                    return false;
            }
        }

        return true;
    }
}
