package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("quarters|q")
public class TrustCommand extends BaseCommand {
    @Subcommand("trust")
    @Description("Give someone else access to your quarter")
    @CommandPermission("quarters.command.trust")
    @CommandCompletion("@players")
    public void onTrust(Player player, OnlinePlayer target) {
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player)) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return;
        }

        Quarter quarter = QuartersAPI.getInstance().getQuarterAtLocation(player.getLocation());
        if (!quarter.getOwner().equals(player)) {
            QuartersMessaging.sendErrorMessage(player, "You do not own this quarter");
            return;
        }

        List<Player> trustedList = quarter.getTrustedPlayers();
        trustedList.add(target.getPlayer());
        quarter.setTrustedPlayers(trustedList);
    }
}
