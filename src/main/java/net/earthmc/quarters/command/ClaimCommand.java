package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Objects;

@CommandAlias("quarters|q")
public class ClaimCommand extends BaseCommand {
    @Subcommand("claim")
    @Description("Claim a quarter")
    @CommandPermission("quarters.command.quarters.claim")
    public void onClaim(Player player) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        if (Objects.equals(quarter.getOwnerResident(), resident)) {
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

        sendClaimConfirmation(quarter, resident);
    }

    private void sendClaimConfirmation(Quarter quarter, Resident resident) {
        double currentPrice = quarter.getPrice();
        Player player = resident.getPlayer();

        if (currentPrice > 0) {
            Confirmation.runOnAccept(() -> {
                if (quarter.getPrice() != currentPrice) {
                    QuartersMessaging.sendErrorMessage(player, "Quarter purchase cancelled as the quarter's price has changed");
                    return;
                }

                if (!resident.getAccount().withdraw(quarter.getPrice(), "Payment for quarter " + quarter.getUUID())) {
                    QuartersMessaging.sendErrorMessage(player, "Quarter purchase cancelled as you do not have sufficient funds");
                    return;
                }

                quarter.getTown().getAccount().deposit(quarter.getPrice(), "Payment for quarter " + quarter.getUUID());

                setAndSaveQuarter(quarter, resident);

                QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");

                Location location = player.getLocation();
                QuartersMessaging.sendInfoMessageToTown(quarter.getTown(), player, player.getName() + " has claimed a quarter " + QuartersMessaging.getLocationString(location));
            })
            .setTitle("Purchasing this quarter will cost " + quarter.getPrice() + ", are you sure you want to purchase it?")
            .sendTo(resident.getPlayer());
        } else {
            setAndSaveQuarter(quarter, resident);

            QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");

            Location location = player.getLocation();
            QuartersMessaging.sendInfoMessageToTown(quarter.getTown(), player, player.getName() + " has claimed a quarter " + QuartersMessaging.getLocationString(location));
        }
    }

    private void setAndSaveQuarter(Quarter quarter, Resident resident) {
        quarter.setOwner(resident.getUUID());
        quarter.setClaimedAt(Instant.now().toEpochMilli());
        quarter.setPrice(null);
        quarter.save();
    }
}
