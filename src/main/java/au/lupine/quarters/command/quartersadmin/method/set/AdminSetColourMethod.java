package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class AdminSetColourMethod extends CommandMethod {

    public AdminSetColourMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.set.colour");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (args.length == 0) throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);

        Color colour = args[0].length() > 3 ? parseColourAsHex() : parseColourAsRGB();

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }

    private Color parseColourAsHex() {
        String arg = args[0];

        int hex;
        int length = arg.length();

        try {
            if (length < 6 || length >= 8) {
                throw new Exception();
            } else if (length == 6) {
                hex = Integer.parseInt(arg, 16);
            } else {
                if (arg.charAt(0) == '#') {
                    arg = arg.substring(1, 7);
                } else {
                    throw new Exception();
                }

                hex = Integer.parseInt(arg, 16);
            }
        } catch (Exception e) {
            throw new CommandMethodException(StringConstants.A_NUMBER_YOU_PROVIDED_WAS_INVALID);
        }

        return new Color(hex);
    }

    private Color parseColourAsRGB() {
        try {
            int r = Integer.parseInt(getArgOrThrow(0, "No argument provided for red"));
            int g = Integer.parseInt(getArgOrThrow(1, "No argument provided for green"));
            int b = Integer.parseInt(getArgOrThrow(2, "No argument provided for blue"));

            return new Color(r, g, b);
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException(StringConstants.A_NUMBER_YOU_PROVIDED_WAS_INVALID);
        }
    }
}
