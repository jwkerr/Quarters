package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
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
    public void onSell(Player player, double price) {
        if (price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        Quarter quarterAtLocation = QuartersAPI.getInstance().getQuarter(player);
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player) || quarterAtLocation == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return;
        }

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (!QuarterUtils.hasSufficientPerms(player, town) && quarterAtLocation.getOwner() != resident) {
            QuartersMessaging.sendErrorMessage(player, "You do not have sufficient permissions to sell this quarter");
            return;
        }

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);
        for (Quarter quarter : quarterList) {
            if (quarter.getUUID().equals(quarterAtLocation.getUUID())) {
                quarter.setPrice(price);
                QuarterDataManager.updateQuarterListOfTown(town, quarterList);
                QuartersMessaging.sendSuccessMessage(player, "This quarter has been set for sale for " + price);
            }
        }
    }
}
