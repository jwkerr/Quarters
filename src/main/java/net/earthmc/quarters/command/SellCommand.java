package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class SellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Sell a quarter")
    @CommandPermission("quarters.command.quarters.sell")
    @CommandCompletion("cancel")
    public void onSell(Player player, @Optional String arg) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.sell"))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        assert quarter != null;
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        if (arg != null && arg.equals("cancel")) {
            quarter.setPrice(null);
            quarter.save();
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer for sale");
            return;
        }

        double price;
        try {
            if (arg == null) { // If the user has specified no argument we will set it to the configured default price or to 0
                Double defaultPrice = TownMetadataManager.getDefaultSellPriceOfTown(TownyAPI.getInstance().getTown(player));
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
            return;
        }


        if (price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        if (!TownyEconomyHandler.isActive()) // If the server is using economy then we set it for sale at the designated price, otherwise it defaults to free
            price = 0.0;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        quarter.setPrice(price);
        quarter.save();

        if (TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + TownyEconomyHandler.getFormattedBalance(price));
        } else if (!TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now claimable using /q claim, it will be free to claim as Towny is not currently using an economy plugin");
        }
    }
}
