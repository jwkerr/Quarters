package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
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

        quarter.delete();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
    }
}
