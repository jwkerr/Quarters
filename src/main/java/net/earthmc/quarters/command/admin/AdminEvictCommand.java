package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.EvictCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quartersadmin|qa")
public class AdminEvictCommand extends BaseCommand {
    @Subcommand("evict")
    @Description("Forecefully evict the player that owns the quarter you are standing in")
    @CommandPermission("quarters.command.quartersadmin.evict")
    public void onEvict(Player player) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (quarter.getOwner() == null) {
            QuartersMessaging.sendErrorMessage(player, "This quarter has no owner");
            return;
        }

        EvictCommand.evictQuarterOwner(player, quarter);
    }
}
