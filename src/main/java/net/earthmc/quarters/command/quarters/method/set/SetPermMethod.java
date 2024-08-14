package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.state.ActionType;
import net.earthmc.quarters.object.state.PermLevel;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPermMethod extends CommandMethod {

    public SetPermMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.perm");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        ActionType type;
        try {
            type = ActionType.valueOf(getArgOrThrow(0, "No action type provided").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid action type provided");
        }

        PermLevel level;
        try {
            level = PermLevel.valueOf(getArgOrThrow(1, "No perm level provided").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid perm level provided");
        }

        boolean allowed = Boolean.parseBoolean(getArgOrThrow(2, "No boolean provided"));

        quarter.getPermissions().setPermission(type, level, allowed);
        quarter.save();

        String lowerCaseLevel = level.name().toLowerCase();
        String lowerCaseType = type.name().toLowerCase();

        QuartersMessaging.sendSuccessMessage(player, "Successfully set " + lowerCaseLevel + " " + lowerCaseType + " permissions to " + allowed);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has set a quarter's " + lowerCaseLevel + " " + lowerCaseType + " permissions to " + allowed, player.getLocation());
    }
}
