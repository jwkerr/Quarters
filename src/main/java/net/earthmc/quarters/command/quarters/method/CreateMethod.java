package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.state.CuboidValidity;
import net.earthmc.quarters.object.wrapper.CuboidSelection;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateMethod extends CommandMethod {

    public CreateMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.create");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        SelectionManager sm = SelectionManager.getInstance();
        List<Cuboid> cuboids = sm.getCuboids(player);
        if (cuboids.isEmpty()) {
            CuboidSelection selection = sm.getSelection(player);
            if (selection.getCuboid() != null) {
                cuboids = List.of(selection.getCuboid());
            } else {
                throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);
            }
        }

        Town town = TownyAPI.getInstance().getTown(cuboids.get(0).getCornerOne());
        if (town == null) throw new CommandMethodException("Could not resolve a town from the first selected position");

        if (!town.hasResident(player)) throw new CommandMethodException("You are not part of this town, you can only make quarters in your own town");

        for (Cuboid cuboid : cuboids) {
            CuboidValidity validity = cuboid.checkValidity();
            switch (validity) {
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to create quarter as it contains wilderness");
                case INTERSECTS -> throw new CommandMethodException("Failed to create quarter as it intersects with a pre-existing quarter");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to create quarter as it spans multiple towns");
            }
        }

        List<Quarter> quarterList = QuarterManager.getInstance().getQuarters(town);
        int maxQuarters = ConfigManager.getMaxQuartersPerTown();
        if (maxQuarters > -1 && quarterList.size() == maxQuarters) throw new CommandMethodException("Selected quarter could not be created as " + town.getName() + " will exceed the configured quarter limit of " + maxQuarters);

        Quarter quarter = new Quarter(town, cuboids);

        int maxVolume = ConfigManager.getMaxQuarterVolume();
        if (maxVolume > -1 && quarter.getVolume() > maxVolume) throw new CommandMethodException("This quarter is too large, the max configured volume is " + maxVolume + " blocks");

        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Selected quarter has been successfully created");

        Location location = quarter.getFirstCornerOfFirstCuboid();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has created a quarter", location);
    }
}
