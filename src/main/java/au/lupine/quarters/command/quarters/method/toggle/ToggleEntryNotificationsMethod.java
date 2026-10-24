package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ToggleEntryNotificationsMethod extends CommandMethod {

    public ToggleEntryNotificationsMethod() {
        super("entrynotifications", "quarters.command.quarters.toggle.entrynotifications");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();
        boolean hasEntryNotifications = rmm.hasEntryNotifications(resident);

        rmm.setHasEntryNotifications(resident, !hasEntryNotifications);

        if (hasEntryNotifications) {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.entrynotifications.feedback.disabled");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.entrynotifications.feedback.enabled");
        }
    }
}
