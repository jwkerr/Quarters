package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateMethod extends CommandMethod {

    public CreateMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.create", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        SelectionManager sm = SelectionManager.getInstance();

        List<Cuboid> cuboids = sm.getCuboidsOrSelectionAsCuboid(player);
        if (cuboids.isEmpty()) throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);

        Town town = TownyAPI.getInstance().getTown(cuboids.get(0).getCornerOne());
        if (town == null) throw new CommandMethodException("Could not resolve a town from the first selected position");

        if (!town.hasResident(player)) throw new CommandMethodException("You are not part of this town, you can only make quarters in your own town");

        for (Cuboid cuboid : cuboids) {
            CuboidValidity validity = cuboid.checkValidity();
            switch (validity) {
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("Failed to create quarter as it contains wilderness");
                case INTERSECTS -> throw new CommandMethodException("Failed to create quarter as it intersects with a pre-existing quarter");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("Failed to create quarter as it spans multiple towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("Failed to create quarter as it outside of this world's maximum or minimum height");
                case TOO_LARGE -> throw new CommandMethodException("Failed to create quarter as at least one of its cuboids is too large");
            }
        }

        List<Quarter> quarterList = QuarterManager.getInstance().getQuarters(town);
        int maxQuarters = ConfigManager.getMaxQuartersPerTown();
        if (maxQuarters > -1 && quarterList.size() == maxQuarters) throw new CommandMethodException("Selected quarter could not be created as " + town.getName() + " will exceed the configured quarter limit of " + maxQuarters);

        Quarter quarter = new Quarter(town, cuboids, player.getUniqueId());

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
