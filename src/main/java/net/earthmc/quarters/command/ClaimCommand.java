package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

import java.time.Instant;

@CommandAlias("quarters|q")
public class ClaimCommand extends BaseCommand {
    @Subcommand("claim")
    @Description("Claim a quarter")
    @CommandPermission("quarters.command.quarters.claim")
    public void onClaim(Player player) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;
        if (quarter.getOwnerResident() == resident) {
            QuartersMessaging.sendErrorMessage(player, "You already own this quarter");
            return;
        }

        if (quarter.getPrice() == null) {
            QuartersMessaging.sendErrorMessage(player, "This quarter is not for sale");
            return;
        }

        if (!quarter.isEmbassy() && quarter.getTown() != resident.getTownOrNull()) {
            QuartersMessaging.sendErrorMessage(player, "You cannot buy this quarter as it is not an embassy and it is not part of your town");
            return;
        }

        if (TownyEconomyHandler.isActive() && resident.getAccount().getHoldingBalance() < quarter.getPrice()) {
            QuartersMessaging.sendErrorMessage(player, "You do not have sufficient funds to buy this quarter");
            return;
        }

        if (quarter.getPrice() > 0) {
            resident.getAccount().withdraw(quarter.getPrice(), "Payment for quarter " + quarter.getUUID());
            quarter.getTown().getAccount().deposit(quarter.getPrice(), "Payment for quarter " + quarter.getUUID());
        }

        quarter.setOwner(resident.getUUID());
        quarter.setClaimedAt(Instant.now().toEpochMilli());
        quarter.setPrice(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
    }
}
