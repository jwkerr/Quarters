package au.lupine.quarters.command.quarters.method.delete;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DeletePlotMethod extends CommandMethod {

    public DeletePlotMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.delete.plot", true);
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(player);
        if (townBlock == null) throw new CommandMethodException("You are not standing within a townblock");

        if (!town.equals(townBlock.getTownOrNull())) throw new CommandMethodException("This townblock is not part of your town");

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(townBlock);

        Confirmation.runOnAccept(() -> {
            for (Quarter quarter : quarters) {
                quarter.delete();
            }

            QuartersMessaging.sendSuccessMessage(player, "Successfully deleted all quarters in this townblock");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "has deleted all quarters in a townblock", player.getLocation());
        }).setTitle("Are you sure you want to delete all the quarters in this townblock?")
                .sendTo(player);
    }
}
