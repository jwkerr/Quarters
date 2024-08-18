package net.earthmc.quarters.command.quartersadmin.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSetAnchorMethod extends CommandMethod {

    public AdminSetAnchorMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.set.anchor");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Location location = player.getLocation();
        quarter.setAnchor(location);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_SET_THIS_QUARTERS_ANCHOR_POINT);
    }
}
