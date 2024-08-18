package net.earthmc.quarters.command.quartersadmin.method.toggle;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminToggleEmbassyMethod extends CommandMethod {

    public AdminToggleEmbassyMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.toggle.embassy");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        quarter.setEmbassy(!quarter.isEmbassy());
        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
        }
    }
}
