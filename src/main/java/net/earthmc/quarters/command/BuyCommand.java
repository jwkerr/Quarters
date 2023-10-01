package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("quarters|q")
public class BuyCommand extends BaseCommand {
    @Subcommand("buy")
    @Description("Buy a quarter")
    @CommandPermission("quarters.command.buy")
    public void onBuy(Player player) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        Quarter quarterAtLocation = QuartersAPI.getInstance().getQuarter(player);
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player) || quarterAtLocation == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return;
        }

        // check balance here

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);
        for (Quarter quarter : quarterList) {
            if (quarter.getUUID().equals(quarterAtLocation.getUUID())) {
                quarter.setOwner(resident);
                QuarterDataManager.updateQuarterListOfTown(town, quarterList);
                QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
            }
        }
    }
}
