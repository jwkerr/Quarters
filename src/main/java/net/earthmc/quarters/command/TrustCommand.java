package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("quarters|q")
public class TrustCommand extends BaseCommand {
    @Subcommand("trust")
    @Description("Manage access of other players to a quarter")
    @CommandPermission("quarters.command.quarters.trust")
    @CommandCompletion("add|remove|clear @players")
    public void onTrust(Player player, String arg, @Optional String target) {
        if (!(arg.equals("add") || arg.equals("remove") || arg.equals("clear"))) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return;
        }

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (!Objects.equals(quarter.getOwnerResident(), TownyAPI.getInstance().getResident(player))) {
            QuartersMessaging.sendErrorMessage(player, "You do not own this quarter");
            return;
        }

        Resident targetResident = null;
        if (target != null) {
            targetResident = TownyAPI.getInstance().getResident(target);
            if (targetResident == null || targetResident.isNPC()) {
                QuartersMessaging.sendErrorMessage(player, "Specified player does not exist");
                return;
            }
        }

        List<UUID> trustedList = getTrustedList(player, targetResident, quarter.getTrusted(), arg);

        quarter.setTrusted(trustedList);
        quarter.save();
    }

    public static List<UUID> getTrustedList(Player player, Resident targetResident, List<UUID> trustedList, String arg) {
        switch (arg) {
            case "add":
                if (!trustedList.contains(targetResident.getUUID())) {
                    trustedList.add(targetResident.getUUID());

                    QuartersMessaging.sendSuccessMessage(player, "Specified player has been added to this quarter's trusted list");
                } else {
                    QuartersMessaging.sendErrorMessage(player, "Specified player is already trusted in this quarter");
                }
                break;
            case "remove":
                if (trustedList.contains(targetResident.getUUID())) {
                    trustedList.remove(targetResident.getUUID());

                    QuartersMessaging.sendSuccessMessage(player, "Specified player has been removed from this quarter's trusted list");
                } else {
                    QuartersMessaging.sendErrorMessage(player, "Specified player is not trusted in this quarter");
                }
                break;
            case "clear":
                trustedList.clear();

                QuartersMessaging.sendSuccessMessage(player, "All trusted players have been removed from this quarter");
                break;
            default:
                QuartersMessaging.sendErrorMessage(player, "Invalid argument");
        }

        return trustedList;
    }
}
