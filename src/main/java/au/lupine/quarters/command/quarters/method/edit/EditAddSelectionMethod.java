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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditAddSelectionMethod extends CommandMethod {

    public EditAddSelectionMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.edit.addselection", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
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
            }
        }

        List<Cuboid> currentCuboids = quarter.getCuboids();

        int maxCuboids = ConfigManager.getMaxCuboidsPerQuarter();
        if (maxCuboids > -1 && cuboids.size() + currentCuboids.size() >= maxCuboids) throw new CommandMethodException("Selection could not be added as it will exceed the configured cuboid limit of " + maxCuboids);

        cuboids.addAll(currentCuboids);
        quarter.setCuboids(cuboids);
        quarter.save();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendSuccessMessage(player, "Successfully added your selection to this quarter");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has added " + cuboids.size() + " cuboid(s) to a quarter", quarter.getFirstCornerOfFirstCuboid());
    }
}
