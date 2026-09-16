package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import au.lupine.quarters.object.wrapper.CuboidSelection;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SelectionAddMethod extends CommandMethod {

    public SelectionAddMethod() {
        super("add", "quarters.command.quarters.selection.add");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        SelectionManager sm = SelectionManager.getInstance();

        CuboidSelection selection = sm.getSelection(player);
        Cuboid newCuboid = selection.getCuboid();
        if (newCuboid == null) throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.invalid_selection");

        List<Cuboid> cuboids = sm.getCuboids(player);
        int maxCuboids = ConfigManager.getMaxCuboidsPerQuarter();
        if (maxCuboids > -1 && cuboids.size() == maxCuboids) throw new CommandMethodException(
                "quarters.command.quarters.selection.add.feedback.cuboid_limit",
                Argument.string("max", Integer.toString(maxCuboids))
        );

        CuboidValidity validity = newCuboid.checkValidity();
        switch (validity) {
            case CONTAINS_WILDERNESS -> throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.contains_wilderness");
            case INTERSECTS -> throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.intersects");
            case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.spans_multiple_towns");
            case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.outside_world_bounds");
            case TOO_LARGE -> throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.too_large");
        }

        for (Cuboid addedCuboid : cuboids) {
            if (newCuboid.intersectsWith(addedCuboid)) throw new CommandMethodException("quarters.command.quarters.selection.add.feedback.intersects_selection");
        }

        sm.clearSelection(player);

        cuboids.add(newCuboid);
        sm.setCuboids(player, cuboids);

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.selection.add.feedback.success");
    }
}
