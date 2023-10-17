package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class EvictCommand extends BaseCommand {
    @Subcommand("evict")
    @Description("Evict the player that owns the quarter you are standing in")
    @CommandPermission("quarters.command.quarters.evict")
    public void onEvict(Player player) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.evict"))
            return;

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        Resident owner = quarter.getOwner();

        quarter.setOwner(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "Successfully evicted " + owner.getName());
    }
}
