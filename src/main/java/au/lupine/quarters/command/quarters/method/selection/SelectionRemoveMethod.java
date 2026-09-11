package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SelectionRemoveMethod extends CommandMethod {

    public SelectionRemoveMethod() {
        super("remove", "quarters.command.quarters.selection.remove");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        List<Cuboid> cuboids = SelectionManager.getInstance().getCuboids(player);

        for (Cuboid cuboid : cuboids) {
            if (cuboid.getPlayersInsideBounds().contains(player)) {
                cuboids.remove(cuboid);
                QuartersMessaging.sendSuccessMessage(player, "Successfully removed the cuboid you are standing in from the selection");
                return;
            }
        }

        QuartersMessaging.sendErrorMessage(player, "Could not find any cuboid at your location to remove");
    }
}
