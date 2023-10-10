package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.utils.QuarterUtils;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("quarters|q")
public class SellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Sell a quarter")
    @CommandPermission("quarters.command.sell")
    public void onSell(Player player, @Optional Double price) {
        if (price != null && price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        if (TownyEconomyHandler.isActive()) {
            price = price == null ? 0.0 : price;
        } else {
            price = 0.0;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player);
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player) || quarter == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return;
        }

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (!QuarterUtils.hasSufficientPerms(player, town) && quarter.getOwner() != resident) {
            QuartersMessaging.sendErrorMessage(player, "You do not have sufficient permissions to sell this quarter");
            return;
        }

        quarter.setPrice(price);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + price);

    }
}
