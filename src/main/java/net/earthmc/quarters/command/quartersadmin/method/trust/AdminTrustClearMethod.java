package net.earthmc.quarters.command.quartersadmin.method.trust;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AdminTrustClearMethod extends CommandMethod {

    public AdminTrustClearMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.trust.clear");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        List<UUID> trusted = quarter.getTrusted();
        trusted.clear();

        quarter.setTrusted(trusted);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.ALL_TRUSTED_PLAYERS_HAVE_BEEN_REMOVED_FROM_THIS_QUARTER);
    }
}
