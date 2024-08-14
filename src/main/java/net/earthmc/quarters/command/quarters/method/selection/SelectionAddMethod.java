package net.earthmc.quarters.command.quarters.method.selection;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.state.CuboidValidity;
import net.earthmc.quarters.object.wrapper.CuboidSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SelectionAddMethod extends CommandMethod {

    public SelectionAddMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection.add");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        SelectionManager sm = SelectionManager.getInstance();

        CuboidSelection selection = sm.getSelection(player);
        Cuboid newCuboid = selection.getCuboid();
        if (newCuboid == null) throw new CommandMethodException("You must select two valid positions using the Quarters wand, or by using /quarters pos");

        List<Cuboid> cuboids = sm.getCuboids(player);
        int maxCuboids = ConfigManager.getMaxCuboidsPerQuarter();
        if (maxCuboids > -1 && cuboids.size() == maxCuboids) throw new CommandMethodException("Selection could not be added as it will exceed the configured cuboid limit of " + maxCuboids);

        int maxCuboidVolume = ConfigManager.getMaxCuboidVolume();
        if (maxCuboidVolume > -1 && newCuboid.getVolume() > maxCuboidVolume) throw new CommandMethodException("Selection could not be added as it exceeds the configured cuboid volume limit of " + maxCuboidVolume + " blocks");

        CuboidValidity validity = newCuboid.checkValidity();
        switch (validity) {
            case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to add cuboid as it contains wilderness");
            case INTERSECTS -> throw new CommandMethodException("Failed to add cuboid as it intersects with a pre-existing quarter");
            case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to add cuboid as it spans multiple towns");
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
