package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.QuarterDeleteEvent;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminDeleteMethod extends CommandMethod {

    public AdminDeleteMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.delete");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        boolean deleted = quarter.delete(QuarterDeleteEvent.Cause.ADMIN_COMMAND, player);
        if (deleted) {
            QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
        }
    }
}
