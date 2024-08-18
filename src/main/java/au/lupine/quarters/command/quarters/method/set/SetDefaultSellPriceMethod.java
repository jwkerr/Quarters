package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetDefaultSellPriceMethod extends CommandMethod {

    public SetDefaultSellPriceMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.defaultsellprice", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        double price = Double.parseDouble(getArgOrThrow(0, "You did not specify a price"));
        if (price < 0) throw new CommandMethodException("Price must be greater than or equal to 0");

        TownMetadataManager.getInstance().setDefaultSellPrice(town, price);

        QuartersMessaging.sendSuccessMessage(player, "Successfully changed the default quarter sale price of " + town.getName() + " to " + price);
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "has changed the default quarter sale price of " + town.getName() + " to " + price, null);
    }
}
