package net.earthmc.quarters.command;

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

@CommandAlias("quarters|q")
public class DeleteCommand extends BaseCommand {
    @Subcommand("delete")
    @Description("Delete quarters in a town")
    @CommandPermission("quarters.command.quarters.delete")
    @CommandCompletion("all")
    public void onDelete(Player player, @Optional String arg) {
        if (arg != null && !arg.equals("all")) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.delete"))
            return;

        Town town = TownyAPI.getInstance().getTown(player);
        if (arg != null) {
            TownMetadataManager.setQuarterListOfTown(town, new ArrayList<>());
            QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in " + town.getName());
            return;
        }

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        quarter.delete();

        Location location = player.getLocation();
        QuartersMessaging.sendSuccessMessage(player, "Successfully deleted this quarter");
        QuartersMessaging.sendInfoMessageToTown(town, player, player.getName() + " has deleted a quarter " + QuartersMessaging.getLocationString(location));
    }
}
