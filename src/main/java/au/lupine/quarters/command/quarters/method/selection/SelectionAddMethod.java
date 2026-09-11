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
        if (newCuboid == null) throw new CommandMethodException("You must select two valid positions using the Quarters wand, or by using /quarters pos");

        List<Cuboid> cuboids = sm.getCuboids(player);
        int maxCuboids = ConfigManager.getMaxCuboidsPerQuarter();
        if (maxCuboids > -1 && cuboids.size() == maxCuboids) throw new CommandMethodException("Selection could not be added as it will exceed the configured cuboid limit of " + maxCuboids);

        CuboidValidity validity = newCuboid.checkValidity();
        switch (validity) {
            case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to add cuboid as it contains wilderness");
            case INTERSECTS -> throw new CommandMethodException("Failed to add cuboid as it intersects with a pre-existing quarter");
            case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to add cuboid as it spans multiple towns");
            case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("Failed to add cuboid as it is outside of this world's maximum or minimum height");
            case TOO_LARGE -> throw new CommandMethodException("Failed to add cuboid as it is too large");
        }

        for (Cuboid addedCuboid : cuboids) {
            if (newCuboid.intersectsWith(addedCuboid)) throw new CommandMethodException("Could not add the current selection as it intersects with a cuboid that has already been added");
        }

        sm.clearSelection(player);

        cuboids.add(newCuboid);
        sm.setCuboids(player, cuboids);

        QuartersMessaging.sendSuccessMessage(player, "Successfully added cuboid to selection");
    }
}
