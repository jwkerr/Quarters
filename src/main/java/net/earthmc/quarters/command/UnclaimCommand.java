package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("quarters|q")
public class UnclaimCommand extends BaseCommand {
    @Subcommand("unclaim")
    @Description("Unclaim a quarter that you own")
    @CommandPermission("quarters.command.quarters.unclaim")
    public void onUnclaim(Player player) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (!Objects.equals(quarter.getOwnerResident(), resident)) {
            QuartersMessaging.sendErrorMessage(player, "You do not own this quarter");
            return;
        }

        quarter.setOwner(null);
        quarter.setClaimedAt(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "You have successfully unclaimed this quarter");
    }
}
