package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.utils.CommandUtils;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class SellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Sell a quarter")
    @CommandPermission("quarters.command.quarters.sell")
    public void onSell(Player player, @Optional Double price) {
        if (!CommandUtils.hasPermission(player, "quarters.action.sell"))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!CommandUtils.isPlayerInQuarter(player, quarter))
            return;

        assert quarter != null;
        if (!CommandUtils.isQuarterInPlayerTown(player, quarter))
            return;

        if (quarter.getPrice() >= 0) { // Set price to -1 if it is already for sale, so it is no longer for sale
            quarter.setPrice(-1);
            quarter.save();
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

        if (TownyEconomyHandler.isActive() && price != 0) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + TownyEconomyHandler.getFormattedBalance(price));
        } else if (!TownyEconomyHandler.isActive()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now claimable using /q buy, it will be free as Towny is not using an economy plugin");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now claimable using /q buy");
        }

    }
}
