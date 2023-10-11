package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class PosCommand extends BaseCommand {
    @Subcommand("pos1")
    @Description("Select 1st position of a quarter")
    @CommandPermission("quarters.command.quarters.pos")
    public void onPos1(Player player) {
        SelectionManager.selectPosition(player, player.getLocation(), true);
    }

    @Subcommand("pos2")
    @Description("Select 2nd position of a quarter")
    @CommandPermission("quarters.command.quarters.pos")
    public void onPos2(Player player) {
        SelectionManager.selectPosition(player, player.getLocation(), false);
    }
}
