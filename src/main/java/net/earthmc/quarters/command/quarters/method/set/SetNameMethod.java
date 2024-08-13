package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNameMethod extends CommandMethod {

    public SetNameMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.name");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_EDIT_THIS_QUARTER);

        if (args.length == 0) throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
        String name = String.join(" ", args);

        int maxNameLength = 32;
        if (name.length() > maxNameLength) throw new CommandMethodException("Specified name is too long, max length is " + maxNameLength);

        quarter.setName(name);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed this quarter's name to " + name);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, player.getName() + " has changed a quarter's name to " + name, quarter.getFirstCornerOfFirstCuboid());
    }
}
