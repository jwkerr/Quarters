package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class Pos2Command extends BaseCommand {
    @Subcommand("pos2")
    @Description("Select 2nd position of a quarter")
    @CommandPermission("quarters.command.pos")
    public void onPos2(Player player) {
        Location location = player.getLocation();

        SelectionManager.selectPosition(player, location, false);
    }
}
