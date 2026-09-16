package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class EditRemoveMethod extends CommandMethod {

    public EditRemoveMethod() {
        super("remove", "quarters.command.quarters.edit.remove", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        Cuboid cuboid = quarter.getCuboidAtPlayer(player);
        List<Cuboid> cuboids = quarter.getCuboids();

        if (cuboids.size() == 1) throw new CommandMethodException("quarters.command.quarters.edit.remove.feedback.only_cuboid");

        cuboids.remove(cuboid);
        quarter.setCuboids(cuboids);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.edit.remove.feedback.success");
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "quarters.command.quarters.edit.remove.feedback.town", player.getLocation());
    }
}
