package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
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
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("quarters.command.quarters.edit.addselection.feedback.contains_wilderness");
                case INTERSECTS -> throw new CommandMethodException("quarters.command.quarters.edit.addselection.feedback.intersects");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("quarters.command.quarters.edit.addselection.feedback.spans_multiple_towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("quarters.command.quarters.edit.addselection.feedback.outside_world_bounds");
                case TOO_LARGE -> throw new CommandMethodException("quarters.command.quarters.edit.addselection.feedback.too_large_cuboid");
            }
        }

        List<Cuboid> currentCuboids = quarter.getCuboids();

        int maxCuboids = Quarters.getInstance().config().quarters.maxCuboidsPerQuarter;
        if (maxCuboids > -1 && cuboids.size() + currentCuboids.size() >= maxCuboids) throw new CommandMethodException(
                "quarters.command.quarters.edit.addselection.feedback.cuboid_limit",
                Argument.string("max", Integer.toString(maxCuboids))
        );

        int addedCuboids = cuboids.size();
        cuboids.addAll(currentCuboids);
        quarter.setCuboids(cuboids);
        quarter.save();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.edit.addselection.feedback.success");
        QuartersMessaging.sendCommandFeedbackToTown(
                quarter.getTown(),
                player,
                "quarters.command.quarters.edit.addselection.feedback.town",
                quarter.getFirstCornerOfFirstCuboid(),
                Argument.string("amount", Integer.toString(addedCuboids))
        );
    }
}
