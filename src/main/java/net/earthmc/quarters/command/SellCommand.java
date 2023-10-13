package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class SellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Sell a quarter")
    @CommandPermission("quarters.command.quarters.sell")
    public void onSell(Player player, @Optional Double price) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.sell"))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!CommandUtil.isPlayerInQuarter(player, quarter))
            return;

        assert quarter != null;
        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        if (quarter.getPrice() != null && price == null) { // Set price to null if it is already for sale, so it is no longer for sale
            quarter.setPrice(null);
            quarter.save();
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer for sale");
            return;
        }

        if (price != null && price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        if (TownyEconomyHandler.isActive()) { // If the server is using economy then we set it for sale at the designated price, otherwise it defaults to free
            price = price == null ? 0.0 : price;
        } else {
            price = 0.0;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        quarter.setPrice(price);
        quarter.save();

        if (TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + TownyEconomyHandler.getFormattedBalance(price));
        } else if (!TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now claimable using /q buy, it will be free as Towny is not currently using an economy plugin");
        }
    }
}
