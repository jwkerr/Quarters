package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
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

        int r = Integer.parseInt(getArgOrThrow(0, "No argument provided for red"));
        int g = Integer.parseInt(getArgOrThrow(1, "No argument provided for green"));
        int b = Integer.parseInt(getArgOrThrow(2, "No argument provided for blue"));

        Color colour = new Color(r, g, b);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_CHANGED_THIS_QUARTERS_COLOUR);
    }
}
