package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ToggleSellOnDeleteMethod extends CommandMethod {

    public ToggleSellOnDeleteMethod() {
        super("sellondelete", "quarters.command.quarters.toggle.sellondelete", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        TownMetadataManager tmm = TownMetadataManager.getInstance();
        boolean sellOnDelete = tmm.getSellOnDelete(town);

        tmm.setSellOnDelete(town, !sellOnDelete);

        if (!sellOnDelete) {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.sellondelete.feedback.enabled");
            QuartersMessaging.sendCommandFeedbackToTown(
                    town,
                    player,
                    "quarters.command.quarters.toggle.sellondelete.feedback.town.enabled",
                    player.getLocation(),
                    Argument.string("town", town.getName())
            );
        } else {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.sellondelete.feedback.disabled");
            QuartersMessaging.sendCommandFeedbackToTown(
                    town,
                    player,
                    "quarters.command.quarters.toggle.sellondelete.feedback.town.disabled",
                    player.getLocation(),
                    Argument.string("town", town.getName())
            );
        }
    }
}
