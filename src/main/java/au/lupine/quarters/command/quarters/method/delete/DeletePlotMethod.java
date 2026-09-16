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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DeletePlotMethod extends CommandMethod {

    public DeletePlotMethod() {
        super("plot", "quarters.command.quarters.delete.plot", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(player);
        if (townBlock == null) throw new CommandMethodException("quarters.command.quarters.delete.plot.feedback.not_in_townblock");

        if (!town.equals(townBlock.getTownOrNull())) throw new CommandMethodException("quarters.command.quarters.delete.plot.feedback.townblock_not_in_town");

        List<Quarter> quarters = QuarterManager.getInstance().getQuarters(townBlock);

        Confirmation.runOnAccept(() -> {
            for (Quarter quarter : quarters) {
                quarter.delete();
            }

            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.delete.plot.feedback.success");
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.delete.plot.feedback.town", player.getLocation());
        }).setTitle(QuartersMessaging.translate(player, "quarters.command.quarters.delete.plot.confirmation.title"))
                .sendTo(player);
    }
}
