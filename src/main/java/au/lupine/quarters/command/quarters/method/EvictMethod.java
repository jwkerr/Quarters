package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class EvictMethod extends CommandMethod {

    public EvictMethod() {
        super("evict", "quarters.command.quarters.evict", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        UUID owner = quarter.getOwner();
        if (owner == null) throw new CommandMethodException(StringConstants.THIS_QUARTER_HAS_NO_OWNER);

        quarter.setOwner(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, StringConstants.YOU_HAVE_SUCCESSFULLY_EVICTED_THIS_QUARTERS_OWNER);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has evicted the owner of a quarter", player.getLocation());
    }
}
