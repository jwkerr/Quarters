package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("quarters|q")
public class TrustCommand extends BaseCommand {
    @Subcommand("trust")
    @Description("Manage access of other players to a quarter")
    @CommandPermission("quarters.command.trust")
    @CommandCompletion("add|remove|clear @players")
    public void onTrust(Player player, String method, @Optional String target) {
        if (!(method.equals("add") || method.equals("remove") || method.equals("clear"))) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player) || quarter == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return;
        }

        if (quarter.getOwner() != null && !quarter.getOwner().equals(TownyAPI.getInstance().getResident(player))) {
            QuartersMessaging.sendErrorMessage(player, "You do not own this quarter");
            return;
        }

        Resident targetResident = TownyAPI.getInstance().getResident(target);
        if (targetResident == null || targetResident.isNPC()) {
            QuartersMessaging.sendErrorMessage(player, "Specified player does not exist");
            return;
        }

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "Could not resolve a town from your current location");
            return;
        }

        List<Resident> trustedList = quarter.getTrustedResidents();
        switch (method) {
            case "add":
                if (!trustedList.contains(targetResident)) {
                    trustedList.add(targetResident);

                    QuartersMessaging.sendSuccessMessage(player, "Specified player has been added to this quarter's trusted list");
                } else {
                    QuartersMessaging.sendErrorMessage(player, "Specified player is already trusted in this quarter");
                    return;
                }

                break;

            case "remove":
                if (trustedList.contains(targetResident)) {
                    trustedList.remove(targetResident);

                    QuartersMessaging.sendSuccessMessage(player, "Specified player has been removed from this quarter's trusted list");
                } else {
                    QuartersMessaging.sendErrorMessage(player, "Specified player is not trusted in this quarter");
                    return;
                }

                break;

            case "clear":
                trustedList.clear();

                QuartersMessaging.sendSuccessMessage(player, "All trusted players have been removed from this quarter");
                break;
        }

        quarter.setTrustedResidents(trustedList);
        quarter.save();
    }
}
