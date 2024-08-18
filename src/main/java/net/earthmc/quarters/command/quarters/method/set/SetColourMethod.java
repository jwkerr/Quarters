package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class SetColourMethod extends CommandMethod {

    public SetColourMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.colour");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        int r = Integer.parseInt(getArgOrThrow(0, "No argument provided for red"));
        int g = Integer.parseInt(getArgOrThrow(1, "No argument provided for green"));
        int b = Integer.parseInt(getArgOrThrow(2, "No argument provided for blue"));

        Color colour = new Color(r, g, b);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }
}
