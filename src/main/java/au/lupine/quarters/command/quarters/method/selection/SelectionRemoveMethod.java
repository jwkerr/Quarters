package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SelectionRemoveMethod extends CommandMethod {

    public SelectionRemoveMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection.remove");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);

        for (Cuboid cuboid : cuboids) {
            if (cuboid.getPlayersInBounds().contains(player)) {
                cuboids.remove(cuboid);
                QuartersMessaging.sendSuccessMessage(player, "Successfully removed the cuboid you are standing in from the selection");
                return;
            }
        }

        QuartersMessaging.sendErrorMessage(player, "Could not find any cuboid at your location to remove");
    }
}
