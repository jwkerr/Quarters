package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CreateMethod extends CommandMethod {

    public CreateMethod() {
        super("create", "quarters.command.quarters.create", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        SelectionManager sm = SelectionManager.getInstance();

        List<Cuboid> cuboids = sm.getCuboidsOrSelectionAsCuboid(player);
        if (cuboids.isEmpty()) throw new CommandMethodException(StringConstants.YOU_HAVE_NOT_SELECTED_ANY_AREAS);

        Town town = TownyAPI.getInstance().getTown(cuboids.getFirst().getCornerOne());
        if (town == null) throw new CommandMethodException("quarters.command.quarters.create.feedback.no_town");

        if (!town.hasResident(player)) throw new CommandMethodException("quarters.command.quarters.create.feedback.not_in_town");

        for (Cuboid cuboid : cuboids) {
            CuboidValidity validity = cuboid.checkValidity();
            switch (validity) {
                case CONTAINS_WILDERNESS -> throw new CommandMethodException("quarters.command.quarters.create.feedback.contains_wilderness");
                case INTERSECTS -> throw new CommandMethodException("quarters.command.quarters.create.feedback.intersects");
                case SPANS_MULTIPLE_TOWNS -> throw new CommandMethodException("quarters.command.quarters.create.feedback.spans_multiple_towns");
                case OUTSIDE_WORLD_BOUNDS -> throw new CommandMethodException("quarters.command.quarters.create.feedback.outside_world_bounds");
                case TOO_LARGE -> throw new CommandMethodException("quarters.command.quarters.create.feedback.too_large_cuboid");
            }
        }

        List<Quarter> quarterList = QuarterManager.getInstance().getQuarters(town);
        int maxQuarters = Quarters.getInstance().config().quarters.maxQuartersPerTown;
        if (maxQuarters > -1 && quarterList.size() >= maxQuarters) throw new CommandMethodException(
                "quarters.command.quarters.create.feedback.town_limit",
                Argument.string("town", town.getName()),
                Argument.string("max", Integer.toString(maxQuarters))
        );

        Quarter quarter = new Quarter(town, cuboids, player.getUniqueId());

        int maxVolume = Quarters.getInstance().config().quarters.maxQuarterVolume;
        if (maxVolume > -1 && quarter.getVolume() > maxVolume) throw new CommandMethodException(
                "quarters.command.quarters.create.feedback.max_volume",
                Argument.string("max", Integer.toString(maxVolume))
        );

        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.create.feedback.success");

        Location location = quarter.getFirstCornerOfFirstCuboid();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.create.feedback.town", location);
    }

}
