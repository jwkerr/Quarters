package net.earthmc.quarters.command.quarters.method.set;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
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

        if (!QuarterManager.getInstance().isPlayerInQuarter(player)) throw new CommandMethodException("You are not standing within a quarter");

        Quarter quarter = QuarterManager.getInstance().getQuarter(player.getLocation());
        if (quarter == null) return;

        if (!quarter.hasPermission(player)) throw new CommandMethodException("You do not have permission to edit this quarter");

        int r = Integer.parseInt(getArgOrThrow(0, "No argument provided for red"));
        int g = Integer.parseInt(getArgOrThrow(1, "No argument provided for green"));
        int b = Integer.parseInt(getArgOrThrow(2, "No argument provided for blue"));

        Color colour = new Color(r, g, b);

        quarter.setColour(colour);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed this quarter's colour");
    }
}
