package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class DeleteCommand extends BaseCommand {
    @Subcommand("delete")
    @Description("Delete the quarter you are standing in")
    @CommandPermission("quarters.command.quarters.delete")
    public void onDelete(Player player) {
        if (!CommandUtil.hasPermission(player, "quarters.action.delete"))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!CommandUtil.isPlayerInQuarter(player, quarter))
            return;

        assert quarter != null;
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        quarter.delete();
    }
}
