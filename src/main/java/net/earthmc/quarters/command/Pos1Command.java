package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class Pos1Command extends BaseCommand {
    @Subcommand("pos1")
    @Description("Select 1st position of a quarter")
    @CommandPermission("quarters.command.pos")
    public void onPos1(Player player) {
        Location location = player.getLocation();

        SelectionManager.selectPosition(player, location, true);
    }
}
