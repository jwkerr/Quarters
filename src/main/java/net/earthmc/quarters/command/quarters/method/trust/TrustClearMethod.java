package net.earthmc.quarters.command.quarters.method.trust;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TrustClearMethod extends CommandMethod {

    public TrustClearMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.trust.clear");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        List<UUID> trusted = quarter.getTrusted();
        trusted.clear();

        quarter.setTrusted(trusted);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.ALL_TRUSTED_PLAYERS_HAVE_BEEN_REMOVED_FROM_THIS_QUARTER);
    }
}
