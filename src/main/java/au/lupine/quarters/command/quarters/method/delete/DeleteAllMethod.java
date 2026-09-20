package au.lupine.quarters.command.quarters.method.delete;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.QuarterDeleteCause;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DeleteAllMethod extends CommandMethod {

    public DeleteAllMethod() {
        super("all", "quarters.command.quarters.delete.all", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        Confirmation.runOnAccept(() -> {
            for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
                quarter.delete(source.getSender(), QuarterDeleteCause.DELETE_ALL_COMMAND);
            }
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.delete.all.feedback.success", Argument.string("town", town.getName()));
            QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.delete.all.feedback.town", null, Argument.string("town", town.getName()));
        }).setTitle(QuartersMessaging.translate(player, "quarters.command.quarters.delete.all.confirmation.title", Argument.string("town", town.getName())))
                .sendTo(player);
    }
}
