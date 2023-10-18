package net.earthmc.quarters.command.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.command.SellCommand;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quartersadmin|qa")
public class AdminSellCommand extends BaseCommand {
    @Subcommand("sell")
    @Description("Forcefully sell a quarter")
    @CommandPermission("quarters.command.quartersadmin.sell")
    @CommandCompletion("cancel")
    public void onSell(Player player, @Optional @Single String arg) {
        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (arg != null && arg.equals("cancel")) {
            SellCommand.cancelQuarterSale(player, quarter);
            return;
        }

        Double price = SellCommand.getSellPrice(player, arg);
        if (price == null)
            return;

        if (price < 0) {
            QuartersMessaging.sendErrorMessage(player, "Price must be greater than or equal to 0");
            return;
        }

        SellCommand.setQuarterForSale(player, quarter, price);
    }
}
