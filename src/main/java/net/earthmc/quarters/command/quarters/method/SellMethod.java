package net.earthmc.quarters.command.quarters.method;

import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.TownMetadataManager;
import net.earthmc.quarters.object.base.CommandMethod;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.exception.CommandMethodException;
import net.earthmc.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellMethod extends CommandMethod {

    public SellMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.sell", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);
        Town town = quarter.getTown();

        String arg = getArgOrNull(0);
        if (arg == null) {
            double defaultSellPrice = TownMetadataManager.getInstance().getDefaultSellPrice(town);
            String formatted = TownyEconomyHandler.getFormattedBalance(defaultSellPrice);

            quarter.setPrice(defaultSellPrice);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "This quarter is now for sale for " + formatted);
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has set a quarter for sale for " + formatted, player.getLocation());
            return;
        }

        if (arg.equalsIgnoreCase("cancel")) {
            if (!quarter.isForSale()) throw new CommandMethodException("This quarter is not for sale");

            quarter.setPrice(null);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer for sale");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has set a quarter not for sale", player.getLocation());
            return;
        }

        double price;
        try {
            price = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandMethodException(StringConstants.A_PROVIDED_ARGUMENT_WAS_INVALID + arg);
        }

        if (price < 0) throw new CommandMethodException("Price must be greater than or equal to 0");

        String formatted = TownyEconomyHandler.getFormattedBalance(price);

        quarter.setPrice(price);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter is now for sale for " + formatted);
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has set a quarter for sale for " + formatted, player.getLocation());
    }
}
