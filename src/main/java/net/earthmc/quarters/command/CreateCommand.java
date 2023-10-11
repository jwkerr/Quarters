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
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.manager.SelectionManager;
import net.earthmc.quarters.utils.CommandUtils;
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
        if (!CommandUtils.hasPermission(player, "quarters.action.create"))
            return;

        Selection selection = SelectionManager.selectionMap.get(player);

        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        if (pos1 == null || pos2 == null) {
            QuartersMessaging.sendErrorMessage(player, "You must select two valid positions using the Quarters wand, or by using /quarters {pos1/pos2}");
            return;
        }

        Cuboid selectedCuboid = new Cuboid(pos1, pos2);
        int maxVolume = Quarters.INSTANCE.getConfig().getInt("max_quarter_volume");
        if (selectedCuboid.getLength() * selectedCuboid.getHeight() * selectedCuboid.getWidth() > maxVolume) {
            QuartersMessaging.sendErrorMessage(player, "Selected area is larger than the server's configured max volume");
            return;
        }

        Town pos1Town = TownyAPI.getInstance().getTown(selection.getPos1());
        if (pos1Town == null) {
            QuartersMessaging.sendErrorMessage(player, "Could not resolve a town from the first selected position");
            return;
        }

        if (!isCuboidInValidLocation(selectedCuboid, player, pos1Town))
            return;

        selection.setPos1(null);
        selection.setPos2(null);

        Quarter newQuarter = new Quarter();
        newQuarter.setPos1(pos1);
        newQuarter.setPos2(pos2);
        newQuarter.setUUID(UUID.randomUUID());
        newQuarter.setTown(pos1Town.getUUID());
        newQuarter.setOwner(null);
        newQuarter.setTrustedResidents(new ArrayList<>());
        newQuarter.setPrice(null);
        newQuarter.setType(QuarterType.APARTMENT);

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(pos1Town);
        if (quarterList == null) {
            quarterList = new ArrayList<>();
        }
        quarterList.add(newQuarter);

        QuarterDataManager.updateQuarterListOfTown(pos1Town, quarterList);

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");
    }

    private boolean isCuboidInValidLocation(Cuboid cuboid, Player player, Town town) {
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);

        for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
            for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                    Location location = new Location(town.getWorld(), x, y, z);

                    if (TownyAPI.getInstance().getTown(location) == null) {
                        QuartersMessaging.sendErrorMessage(player, "Selected area contains wilderness");
                        return false;
                    }

                    Town currentPosTown = TownyAPI.getInstance().getTown(location);
                    if (town != currentPosTown) {
                        QuartersMessaging.sendErrorMessage(player, "Selected area contains multiple towns");
                        return false;
                    }

                    if (quarterList != null) {
                        for (Quarter quarter : quarterList) {
                            Cuboid currentQuarterCuboid = new Cuboid(quarter.getPos1(), quarter.getPos2());

                            if (doCuboidBoundsIntersect(cuboid, currentQuarterCuboid)) {
                                QuartersMessaging.sendErrorMessage(player, "Selected area intersects with a pre-existing quarter");
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean doCuboidBoundsIntersect(Cuboid cuboid1, Cuboid cuboid2) {
        boolean overlapX = (cuboid1.getMaxX() >= cuboid2.getMinX()) && (cuboid2.getMaxX() >= cuboid1.getMinX());
        boolean overlapY = (cuboid1.getMaxY() >= cuboid2.getMinY()) && (cuboid2.getMaxY() >= cuboid1.getMinY());
        boolean overlapZ = (cuboid1.getMaxZ() >= cuboid2.getMinZ()) && (cuboid2.getMaxZ() >= cuboid1.getMinZ());

        return overlapX && overlapY && overlapZ;
    }
}
