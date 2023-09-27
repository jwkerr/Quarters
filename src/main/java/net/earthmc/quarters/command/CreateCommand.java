package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("quarters|q")
public class CreateCommand extends BaseCommand {
    @Subcommand("create")
    @Description("Turn selected area into a quarter")
    @CommandPermission("quarters.command.create")
    public void onCreate(Player player) {
        Selection selection = SelectionManager.selectionMap.get(player);

        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        if (pos1 == null | pos2 == null) {
            QuartersMessaging.sendErrorMessage(player, "You must select two valid positions using the Quarters wand, or by using /quarters {pos1/pos2}");
            return;
        }

        World world = pos1.getWorld();
        Cuboid selectedCuboid = new Cuboid(pos1, pos2);

        int townBlockSize = TownySettings.getTownBlockSize();

        if (selectedCuboid.getMaxX() - selectedCuboid.getMinX() > townBlockSize | selectedCuboid.getMaxZ() - selectedCuboid.getMinZ() > townBlockSize) {
            QuartersMessaging.sendErrorMessage(player, "Selected area is larger than the server's town block size");
            return;
        }

        TownyAPI townyAPI = TownyAPI.getInstance();
        TownBlock pos1TownBlock = TownyAPI.getInstance().getTownBlock(selection.getPos1());
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTownBlock(pos1TownBlock);

        for (int x = selectedCuboid.getMinX(); x <= selectedCuboid.getMaxX(); x++) {
            for (int y = selectedCuboid.getMinY(); y <= selectedCuboid.getMaxY(); y++) {
                for (int z = selectedCuboid.getMinZ(); z <= selectedCuboid.getMaxZ(); z++) {
                    Location location = new Location(world, x, y, z);

                    if (townyAPI.getTown(location) == null) {
                        QuartersMessaging.sendErrorMessage(player, "Selected area contains wilderness");
                        return;
                    }

                    TownBlock currentPosTownBlock = townyAPI.getTownBlock(new Location(world, x, y, z));
                    if (pos1TownBlock != currentPosTownBlock) {
                        QuartersMessaging.sendErrorMessage(player, "Selected area contains multiple town blocks");
                        return;
                    }

                    if (quarterList != null) {
                        for (Quarter quarter : quarterList) {
                            Cuboid currentQuarterCuboid = new Cuboid(quarter.getPos1(), quarter.getPos2());

                            if (doCuboidBoundsIntersect(selectedCuboid, currentQuarterCuboid)) {
                                QuartersMessaging.sendErrorMessage(player, "Selected area intersects with a pre-existing quarter");
                                return;
                            }
                        }
                    }
                }
            }
        }

        selection.setPos1(null);
        selection.setPos2(null);

        Quarter newQuarter = new Quarter();
        newQuarter.setPos1(pos1);
        newQuarter.setPos2(pos2);
        newQuarter.setUUID(UUID.randomUUID());
        newQuarter.setTown(pos1TownBlock.getTownOrNull().getUUID());
        newQuarter.setOwner(null);
        newQuarter.setTrustedPlayers(null);

        List<Quarter> updatedVal = new ArrayList<>();
        if (quarterList != null) {
            updatedVal.addAll(quarterList);
            updatedVal.add(newQuarter);
        } else {
            updatedVal.add(newQuarter);
        }

        QuarterDataManager.updateQuarterListOfTownBlock(pos1TownBlock, updatedVal);

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");
    }

    private boolean doCuboidBoundsIntersect(Cuboid cuboid1, Cuboid cuboid2) {
        boolean overlapX = (cuboid1.getMaxX() >= cuboid2.getMinX()) && (cuboid2.getMaxX() >= cuboid1.getMinX());
        boolean overlapY = (cuboid1.getMaxY() >= cuboid2.getMinY()) && (cuboid2.getMaxY() >= cuboid1.getMinY());
        boolean overlapZ = (cuboid1.getMaxZ() >= cuboid2.getMinZ()) && (cuboid2.getMaxZ() >= cuboid1.getMinZ());

        return overlapX && overlapY && overlapZ;
    }
}
