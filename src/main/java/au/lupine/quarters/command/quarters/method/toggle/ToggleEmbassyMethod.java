package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ToggleEmbassyMethod extends CommandMethod {

    public ToggleEmbassyMethod() {
        super("embassy", "quarters.command.quarters.toggle.embassy", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) throw new CommandMethodException(StringConstants.THIS_QUARTER_IS_NOT_PART_OF_YOUR_TOWN);

        quarter.setEmbassy(!quarter.isEmbassy());
        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has toggled a quarter's embassy status on", player.getLocation());
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has toggled a quarter's embassy status off", player.getLocation());
        }
    }
}
