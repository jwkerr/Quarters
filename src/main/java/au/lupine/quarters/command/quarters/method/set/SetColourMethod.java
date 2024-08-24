package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
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

        int r;
        int g;
        int b;
        try {
            r = Integer.parseInt(getArgOrThrow(0, "No argument provided for red"));
            g = Integer.parseInt(getArgOrThrow(1, "No argument provided for green"));
            b = Integer.parseInt(getArgOrThrow(2, "No argument provided for blue"));
        } catch (NumberFormatException e) {
            throw new CommandMethodException("A number you provided was invalid");
        }

        Color colour = new Color(r, g, b);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }
}
