package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class EmbassyCommand extends BaseCommand {
    @Subcommand("embassy")
    @Description("Toggle a quarter's embassy status")
    @CommandPermission("quarters.command.quarters.embassy")
    public void onEmbassy(Player player) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.embassy"))
            return;

        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        quarter.setEmbassy(!quarter.isEmbassy());

        if (quarter.getOwner().getTownOrNull() != quarter.getTown())
            quarter.setOwner(null);

        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
        }

    }
}
