package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.lifecycle.QuarterCreateEvent;
import au.lupine.quarters.api.event.lifecycle.QuarterPreCreateEvent;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.exception.MultiCommandMethodException;
import au.lupine.quarters.object.state.CuboidValidity;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

public final class CreateMethod extends CommandMethod {

    public CreateMethod() {
        super("create", "quarters.command.quarters.create", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        SelectionManager sm = SelectionManager.getInstance();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        List<Cuboid> cuboids = sm.getCuboidsOrSelectionAsCuboid(player);

        QuarterPreCreateEvent preEvent = new QuarterPreCreateEvent(player, resident, cuboids);
        preEvent.callEvent();
        if (preEvent.isCancelled()) {
            String cancelMessage = preEvent.getCancelMessage();
            if (cancelMessage != null) QuartersMessaging.sendErrorMessage(player, cancelMessage);
            return;
        }

        cuboids = preEvent.getCuboids();
        if (cuboids.isEmpty()) throw new CommandMethodException("quarters.command.quarters.selection.feedback.none");

        List<CommandMethodException> validationErrors = new ArrayList<>();
        Town town = TownyAPI.getInstance().getTown(cuboids.getFirst().getCornerOne());
        if (town == null) {
            validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.no_town"));
        } else if (!town.hasResident(player)) {
            validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.not_in_town"));
        }

        for (Cuboid cuboid : cuboids) {
            for (CuboidValidity validity : cuboid.checkValidity()) {
                switch (validity) {
                    case CONTAINS_WILDERNESS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.contains_wilderness"));
                    case INTERSECTS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.intersects"));
                    case SPANS_MULTIPLE_TOWNS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.spans_multiple_towns"));
                    case OUTSIDE_WORLD_BOUNDS -> validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.outside_world_bounds"));
                    case TOO_LARGE -> validationErrors.add(new CommandMethodException("quarters.command.quarters.create.feedback.too_large_cuboid"));
                    case VALID -> { }
                }
            }
        }

        int maxQuarters = Quarters.getInstance().config().quarters.maxQuartersPerTown;
        int maxVolume = Quarters.getInstance().config().quarters.maxQuarterVolume;
        int volume = cuboids.stream().mapToInt(Cuboid::getVolume).sum();
        if (maxVolume > -1 && volume > maxVolume) {
            validationErrors.add(new CommandMethodException(
                    "quarters.command.quarters.create.feedback.max_volume",
                    Argument.string("max", Integer.toString(maxVolume))
            ));
        }

        if (town != null) {
            List<Quarter> quarterList = QuarterManager.getInstance().getQuarters(town);
            if (maxQuarters > -1 && quarterList.size() >= maxQuarters) {
                validationErrors.add(new CommandMethodException(
                        "quarters.command.quarters.create.feedback.town_limit",
                        Argument.string("town", town.getName()),
                        Argument.string("max", Integer.toString(maxQuarters))
                ));
            }
        }

        if (!validationErrors.isEmpty()) throw new MultiCommandMethodException(validationErrors);

        Quarter quarter = new Quarter(town, cuboids, player.getUniqueId());

        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.create.feedback.success");

        Location location = quarter.getFirstCornerOfFirstCuboid();

        sm.clearSelection(player);
        sm.clearCuboids(player);

        QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.create.feedback.town", location);

        QuarterCreateEvent postEvent = new QuarterCreateEvent(quarter);
        postEvent.callEvent();
    }

}
