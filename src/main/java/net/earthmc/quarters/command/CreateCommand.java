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
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.Selection;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

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
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int townBlockSize = TownySettings.getTownBlockSize();

        if (maxX - minX > townBlockSize | maxZ - minZ > townBlockSize) {
            QuartersMessaging.sendErrorMessage(player, "Selected area is larger than the server's town block size");
            return;
        }

        TownyAPI townyAPI = TownyAPI.getInstance();
        TownBlock pos1TownBlock = TownyAPI.getInstance().getTownBlock(selection.getPos1());
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTownBlock(pos1TownBlock);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
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

                        }
                    }
                }
            }
        }

        selection.setPos1(null);
        selection.setPos2(null);

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");
    }

    private boolean doesBoundIntersect() {

    }
}
