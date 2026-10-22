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

/** Toggles whether new members of a town receive Quarters resident permissions immediately. */
public final class ToggleResidentPermissionDelayMethod extends CommandMethod {

    public ToggleResidentPermissionDelayMethod() {
        super("residentdelay", "quarters.command.quarters.toggle.residentdelay", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) throw new CommandMethodException(StringConstants.YOU_ARE_NOT_PART_OF_A_TOWN);

        TownMetadataManager metadata = TownMetadataManager.getInstance();
        boolean enabled = metadata.isResidentPermissionDelayEnabled(town);
        metadata.setResidentPermissionDelayEnabled(town, !enabled);

        String state = enabled ? "disabled" : "enabled";
        QuartersMessaging.sendCommandFeedbackToTown(
                town,
                player,
                "quarters.command.quarters.toggle.residentdelay.feedback.town." + state,
                player.getLocation(),
                Argument.string("town", town.getName())
        );
    }
}
