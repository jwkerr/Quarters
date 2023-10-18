package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.TrustCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("quartersadmin|qa")
public class AdminTrustCommand extends BaseCommand {
    @Subcommand("trust")
    @Description("Forcefully manage access of other players to a quarter")
    @CommandPermission("quarters.command.quartersadmin.trust")
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

        Resident targetResident = TownyAPI.getInstance().getResident(target);
        if (targetResident == null || targetResident.isNPC()) {
            QuartersMessaging.sendErrorMessage(player, "Specified player does not exist");
            return;
        }

        List<Resident> trustedList = TrustCommand.getTrustedList(player, targetResident, quarter.getTrustedResidents(), arg);

        quarter.setTrustedResidents(trustedList);
        quarter.save();
    }
}
