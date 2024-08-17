package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimMethod extends CommandMethod {

    public ClaimMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.claim");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Quarter quarter = getQuarterAtPlayerOrByUUIDOrThrow(player, getArgOrNull(0));

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        canResidentClaimQuarter(resident, quarter);

        sendClaimConfirmation(resident, quarter);
    }

    private void canResidentClaimQuarter(Resident resident, Quarter quarter) {
        Double price = quarter.getPrice();
        if (price == null) throw new CommandMethodException("This quarter is not for sale");

        if (quarter.isResidentOwner(resident)) throw new CommandMethodException("You already own this quarter");

        if (!quarter.isEmbassy() && !quarter.getTown().equals(resident.getTownOrNull())) throw new CommandMethodException("You cannot buy this quarter as it is not an embassy and it is not part of your town");

        if (resident.getAccount().getHoldingBalance() < price) throw new CommandMethodException("You do not have sufficient funds to buy this quarter");
    }

    private void sendClaimConfirmation(Resident resident, Quarter quarter) {
        Double currentPrice = quarter.getPrice();
        if (currentPrice == null) return; // This should never happen given prior state

        Player player = resident.getPlayer();
        if (player == null) return;

        String formattedPrice = TownyEconomyHandler.getFormattedBalance(currentPrice);

        if (currentPrice > 0) {
            Confirmation.runOnAccept(() -> {
                try {
                    canResidentClaimQuarter(resident, quarter);
                    if (!currentPrice.equals(quarter.getPrice())) throw new CommandMethodException("Failed to buy this quarter as its price has changed");
                } catch (CommandMethodException e) {
                    QuartersMessaging.sendErrorMessage(player, e.getMessage());
                    return;
                }

                String reason = "Quarter " + quarter.getUUID() + " sale";
                resident.getAccount().withdraw(currentPrice, reason);
                quarter.getTown().getAccount().deposit(currentPrice, reason);

                changeOwnerAndSave(quarter, resident);

                QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
                QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has claimed a quarter for " + formattedPrice, player.getLocation());
            })
                    .setTitle("Purchasing this quarter will cost " + formattedPrice + ", are you sure you want to purchase it?")
                    .sendTo(player);
        } else {
            changeOwnerAndSave(quarter, resident);

            QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has claimed a quarter", player.getLocation());
        }
    }

    private void changeOwnerAndSave(Quarter quarter, Resident resident) {
        quarter.setOwner(resident.getUUID());
        quarter.setPrice(null);
        quarter.save();
    }
}
