package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandAlias("quartersadmin|qa")
public class AdminDeleteCommand extends BaseCommand {
    @Subcommand("delete")
    @Description("Forcefully delete quarters in a town")
    @CommandPermission("quarters.command.quartersadmin.delete")
    @CommandCompletion("all")
    public void onDelete(Player player, @Optional String arg) {
        if (arg != null && !arg.equals("all")) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        if (arg != null) {
            Town town = TownyAPI.getInstance().getTown(player.getLocation());
            if (town == null) {
                QuartersMessaging.sendErrorMessage(player, "You must be standing in the town you want to delete all the quarters of");
                return;
            }

            TownMetadataManager.setQuarterListOfTown(town, new ArrayList<>());
            QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in " + town.getName());
            return;
        }

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        quarter.delete();
        QuartersMessaging.sendSuccessMessage(player, "Successfully deleted this quarter");
    }
}
