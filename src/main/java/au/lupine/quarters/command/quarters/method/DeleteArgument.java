package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.command.quarters.method.delete.DeleteAllMethod;
import au.lupine.quarters.command.quarters.method.delete.DeletePlotMethod;
import au.lupine.quarters.object.base.CommandArgument;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.QuarterDeleteCause;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DeleteArgument extends CommandArgument {

    public DeleteArgument() {
        super("delete", "quarters.command.quarters.delete", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new DeleteAllMethod().build())
                .then(new DeletePlotMethod().build());
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        quarter.delete(source.getSender(), QuarterDeleteCause.DELETE_COMMAND);

        QuartersMessaging.sendSuccessMessage(player, StringConstants.SUCCESSFULLY_DELETED_THIS_QUARTER);
        QuartersMessaging.sendCommandFeedbackToTown(town, player, "quarters.command.quarters.delete.feedback.town", player.getLocation());
    }
}
