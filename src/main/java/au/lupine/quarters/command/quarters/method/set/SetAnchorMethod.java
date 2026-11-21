package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SetAnchorMethod extends CommandMethod {

    public SetAnchorMethod() {
        super("anchor", "quarters.command.quarters.set.anchor");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException("quarters.command.feedback.no_action_permission");

        Location location = player.getLocation();
        quarter.setAnchor(location);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.anchor.feedback.success");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "quarters.command.quarters.set.anchor.feedback.town", location);
    }
}
