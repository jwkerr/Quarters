package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSellMethod extends CommandMethod {

    public AdminSellMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.sell");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        Town town = quarter.getTown();

        String arg = getArgOrNull(0);
        if (arg == null) {
            double defaultSellPrice = TownMetadataManager.getInstance().getDefaultSellPrice(town);
            String formatted = TownyEconomyHandler.getFormattedBalance(defaultSellPrice);

            quarter.setPrice(defaultSellPrice);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "This quarter is now for sale for " + formatted);
            return;
        }

        if (arg.equalsIgnoreCase("cancel")) {
            if (!quarter.isForSale()) throw new CommandMethodException("This quarter is not for sale");

            quarter.setPrice(null);
            quarter.save();

            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer for sale");
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
    }
}
