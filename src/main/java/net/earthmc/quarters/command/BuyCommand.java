package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class BuyCommand extends BaseCommand {
    @Subcommand("buy")
    @Description("Buy a quarter")
    @CommandPermission("quarters.command.quarters.buy")
    public void onBuy(Player player) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(player.getLocation());
        if (!CommandUtil.isPlayerInQuarter(player, quarter))
            return;

        assert quarter != null;
        if (quarter.getPrice() <= -1) {
            QuartersMessaging.sendErrorMessage(player, "This quarter is not for sale");
            return;
        }

        if (TownyEconomyHandler.isActive() && resident.getAccount().getHoldingBalance() < quarter.getPrice()) {
            QuartersMessaging.sendErrorMessage(player, "You do not have sufficient funds to buy this quarter");
            return;
        }

        if (quarter.getPrice() > 0)
            resident.getAccount().withdraw(quarter.getPrice(), "Payment for quarter " + quarter.getUUID());

        quarter.setOwner(resident);
        quarter.setPrice(null);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "You are now the owner of this quarter");
    }
}
