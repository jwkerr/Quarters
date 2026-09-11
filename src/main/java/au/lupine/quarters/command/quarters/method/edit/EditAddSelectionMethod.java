package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class EditAddSelectionMethod extends CommandMethod {

    public EditAddSelectionMethod() {
        super("addselection", "quarters.command.quarters.edit.addselection", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        SelectionManager sm = SelectionManager.getInstance();

        List<Cuboid> cuboids = sm.getCuboidsOrSelectionAsCuboid(player);
        if (cuboids.isEmpty()) throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);

        for (Cuboid cuboid : cuboids) {
            CuboidValidity validity = cuboid.checkValidity();
            switch (validity) {
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to add selection as it contains wilderness");
                case INTERSECTS -> throw new CommandMethodException("Failed to add selection as it intersects with a pre-existing quarter");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to add selection as it spans multiple towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("Failed to add selection as it outside of this world's maximum or minimum height");
                case TOO_LARGE -> throw new CommandMethodException("Failed to add selection as at least one of its cuboids is too large");
            }
        }

        List<Cuboid> currentCuboids = quarter.getCuboids();

        int maxCuboids = ConfigManager.getMaxCuboidsPerQuarter();
        if (maxCuboids > -1 && cuboids.size() + currentCuboids.size() >= maxCuboids) throw new CommandMethodException("Selection could not be added as it will exceed the configured cuboid limit of " + maxCuboids);

        int addedCuboids = cuboids.size();
        cuboids.addAll(currentCuboids);
        quarter.setCuboids(cuboids);
        quarter.save();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendSuccessMessage(player, "Successfully added your selection to this quarter");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has added " + addedCuboids + " cuboid(s) to a quarter", quarter.getFirstCornerOfFirstCuboid());
    }
}
