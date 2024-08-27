package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminMetaRemoveMethod extends CommandMethod {

    public AdminMetaRemoveMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.meta.remove");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        String key = getArgOrThrow(0, "No meta key provided", false);

        if (!quarter.removeMetaData(key, true)) throw new CommandMethodException("This quarter has no meta named " + key);

        QuartersMessaging.sendSuccessMessage(player, "Successfully removed meta key " + key + " from this quarter");
    }
}
