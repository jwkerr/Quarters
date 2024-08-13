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

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_EDIT_THIS_QUARTER);

        Location location = player.getLocation();
        quarter.setAnchor(location);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully set this quarter's anchor point to your location");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, player.getName() + " has changed a quarter's anchor point", location);
    }
}
