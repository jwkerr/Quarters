package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EvictMethod extends CommandMethod {

    public EvictMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.evict");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Resident owner = quarter.getOwnerResident();
        if (owner == null) throw new CommandMethodException("This quarter has no owner");

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        String ownerName = owner.getName();

        quarter.setOwner(null);
        quarter.setClaimedAt(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully evicted " + owner.getName());
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, player.getName() + " has evicted " + ownerName + " from a quarter", player.getLocation());
    }
}
