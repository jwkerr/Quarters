package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.state.QuarterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTypeMethod extends CommandMethod {

    public SetTypeMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.type");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        QuarterManager qm = QuarterManager.getInstance();
        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException("You are not standing within a quarter");

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException("This quarter is not in your town");

        QuarterType type;
        try {
            type = QuarterType.valueOf(getArgOrThrow(0, "No quarter type provided"));
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid quarter type provided");
        }

        quarter.setType(type);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set to type: " + quarter.getType());
    }
}
