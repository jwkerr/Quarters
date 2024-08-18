package net.earthmc.quarters.command.quartersadmin.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.state.QuarterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSetTypeMethod extends CommandMethod {

    public AdminSetTypeMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.set.type");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        QuarterType type;
        try {
            type = QuarterType.valueOf(getArgOrThrow(0, "No quarter type provided").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid quarter type provided");
        }

        quarter.setType(type);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set to type: " + quarter.getType().getCommonName());
    }
}
