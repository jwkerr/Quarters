package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersTown;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class SellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Sell a quarter")
    @CommandPermission("quarters.command.quarters.sell")
    @CommandCompletion("{price}|cancel")
    public void onSell(Player player, @Optional @Single String arg) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.sell"))
            return;

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        if (arg != null && arg.equals("cancel")) {
            cancelQuarterSale(player, quarter);
            return;
        }

        Double price = getSellPrice(player, arg);
        if (price == null)
            return;

        if (price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        setQuarterForSale(player, quarter, price);
    }

    public static void cancelQuarterSale(Player player, Quarter quarter) {
        quarter.setPrice(null);
        quarter.save();
        QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer for sale");
    }

    public static Double getSellPrice(Player player, String arg) {
        double price;
        try {
            if (arg == null) { // If the user has specified no argument we will set it to the configured default price or to 0
                QuartersTown quartersTown = new QuartersTown(TownyAPI.getInstance().getTown(player));
                Double defaultPrice = quartersTown.getDefaultSellPrice();
                if (defaultPrice != null) {
                    price = defaultPrice;
                } else {
                    price = 0.0;
                }
            } else {
                price = Double.parseDouble(arg);
            }
        } catch (NumberFormatException e) {
            QuartersMessaging.sendErrorMessage(player, "Invalid argument");
            return null;
        }

        return price;
    }

    public static void setQuarterForSale(Player player, Quarter quarter, double price) {
        if (!TownyEconomyHandler.isActive()) // If the server is using economy then we set it for sale at the designated price, otherwise it defaults to free
            price = 0.0;

        quarter.setPrice(price);
        quarter.save();

        if (TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + TownyEconomyHandler.getFormattedBalance(price));
        } else if (!TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now claimable using /q claim, it will be free to claim as Towny is not currently using an economy plugin");
        }
    }
}
