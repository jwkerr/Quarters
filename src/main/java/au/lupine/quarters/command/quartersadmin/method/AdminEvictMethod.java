package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdminEvictMethod extends CommandMethod {

    public AdminEvictMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.evict");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        UUID owner = quarter.getOwner();
        if (owner == null) throw new CommandMethodException(StringConstants.THIS_QUARTER_HAS_NO_OWNER);

        quarter.setOwner(null);

        QuartersMessaging.sendSuccessMessage(player, StringConstants.YOU_HAVE_SUCCESSFULLY_EVICTED_THIS_QUARTERS_OWNER);
    }
}
