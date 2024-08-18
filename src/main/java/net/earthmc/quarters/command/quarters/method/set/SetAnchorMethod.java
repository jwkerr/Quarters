package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAnchorMethod extends CommandMethod {

    public SetAnchorMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.anchor");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        Location location = player.getLocation();
        quarter.setAnchor(location);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_SET_THIS_QUARTERS_ANCHOR_POINT);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has changed a quarter's anchor point", location);
    }
}
