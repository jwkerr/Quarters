package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class PosCommand extends BaseCommand {
    @Subcommand("pos1")
    @Description("Select 1st position of a quarter")
    @CommandPermission("quarters.command.quarters.pos")
    @CommandCompletion("x y z")
    public void onPos1(Player player, @Default("0") int x, @Default("0") int y, @Default("0") int z) {
        Location location = adjustLocation(player.getLocation(), x, y, z);

        SelectionManager.selectPosition(player, location, true);
    }

    @Subcommand("pos2")
    @Description("Select 2nd position of a quarter")
    @CommandPermission("quarters.command.quarters.pos")
    @CommandCompletion("x y z")
    public void onPos2(Player player, @Default("0") int x, @Default("0") int y, @Default("0") int z) {
        Location location = adjustLocation(player.getLocation(), x, y, z);

        SelectionManager.selectPosition(player, location, false);
    }

    private Location adjustLocation(Location playerLocation, int x, int y, int z) {
        return playerLocation.add(x, y, z);
    }
}
