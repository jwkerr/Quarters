package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class DefaultSellPriceCommand extends BaseCommand {
    @Subcommand("defaultsellprice")
    @Description("Set the default sell price of quarters in your town")
    @CommandPermission("quarters.command.quarters.defaultsellprice")
    public void onDefaultSellPrice(Player player, double price) {
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not part of a town");
            return;
        }

        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.defaultsellprice"))
            return;

        TownMetadataManager.setDefaultSellPriceOfTown(town, price);

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed the default quarter sale price of " + town.getName() + " to " + price);
    }
}
