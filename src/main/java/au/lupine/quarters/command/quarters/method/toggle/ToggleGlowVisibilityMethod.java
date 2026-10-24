package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Toggles whether the quarter may be shown with the selection glow effect.
 */
public final class ToggleGlowVisibilityMethod extends CommandMethod {

    public ToggleGlowVisibilityMethod() {
        super("glowvisibility", "quarters.command.quarters.toggle.glowvisibility", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.isPlayerInTown(player)) {
            throw new CommandMethodException("quarters.command.feedback.quarter_not_part_of_your_town");
        }

        boolean visible = !quarter.isGlowVisible();
        quarter.setHideFromGlow(!visible);
        quarter.save();

        if (visible) {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.glowvisibility.feedback.enabled");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "quarters.command.quarters.toggle.glowvisibility.feedback.town.enabled", player.getLocation());
        } else {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.glowvisibility.feedback.disabled");
            QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "quarters.command.quarters.toggle.glowvisibility.feedback.town.disabled", player.getLocation());
        }
    }
}
