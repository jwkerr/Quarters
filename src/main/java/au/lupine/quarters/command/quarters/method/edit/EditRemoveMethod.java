package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditRemoveMethod extends CommandMethod {

    public EditRemoveMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.edit.remove", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        Cuboid cuboid = quarter.getCuboidAtPlayer(player);
        List<Cuboid> cuboids = quarter.getCuboids();

        if (cuboids.size() == 1) throw new CommandMethodException("This cuboid is the only cuboid in this quarter, please use /q delete instead");

        cuboids.remove(cuboid);
        quarter.setCuboids(cuboids);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully removed the cuboid at your location");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has remove a cuboid from a quarter", player.getLocation());
    }
}
