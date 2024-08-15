package net.earthmc.quarters.command.quarters.method.toggle;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleEmbassyMethod extends CommandMethod {

    public ToggleEmbassyMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.embassy");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        quarter.setEmbassy(!quarter.isEmbassy());
        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has toggled a quarter's embassy status on", player.getLocation());
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has toggled a quarter's embassy status off", player.getLocation());
        }
    }
}
