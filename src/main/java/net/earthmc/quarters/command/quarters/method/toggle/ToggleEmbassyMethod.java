package net.earthmc.quarters.command.quarters.method.toggle;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleEmbassyMethod extends CommandMethod {

    public ToggleEmbassyMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.embassy");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        QuarterManager qm = QuarterManager.getInstance();

        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException("You are not standing within a quarter");

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException("This quarter is not in your town");

        quarter.setEmbassy(!quarter.isEmbassy());
        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
        }
    }
}
