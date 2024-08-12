package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.wrapper.StringConstants;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;

public class ClaimMethod extends CommandMethod {

    public ClaimMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.claim");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        QuarterManager qm = QuarterManager.getInstance();

        if (!qm.isPlayerInQuarter(player)) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_STANDING_WITHIN_A_QUARTER);

        Quarter quarter = qm.getQuarter(player.getLocation());
        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        if (resident.equals(quarter.getOwnerResident())) throw new CommandMethodException("You already own this quarter");

        if (quarter.getPrice() == null) throw new CommandMethodException("This quarter is not for sale");

        if (!quarter.isEmbassy() && !quarter.getTown().equals(resident.getTownOrNull())) throw new CommandMethodException("You cannot buy this quarter as it is not an embassy and it is not part of your town");

        if (TownyEconomyHandler.isActive() && resident.getAccount().getHoldingBalance() < quarter.getPrice()) throw new CommandMethodException("You do not have sufficient funds to buy this quarter");

        sendClaimConfirmation(quarter, resident);
    }

    private void sendClaimConfirmation(Quarter quarter, Resident resident) { // TODO: rewrite this shit
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
                QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, player.getName() + " has claimed a quarter", player.getLocation());
            })
            .setTitle("Purchasing this quarter will cost " + quarter.getPrice() + ", are you sure you want to purchase it?")
            .sendTo(resident.getPlayer());
        } else {
            setAndSaveQuarter(quarter, resident);

            QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, player.getName() + " has claimed a quarter", player.getLocation());
        }
    }

    private void setAndSaveQuarter(Quarter quarter, Resident resident) {
        quarter.setOwner(resident.getUUID());
        quarter.setPrice(null);
        quarter.save();
    }
}
