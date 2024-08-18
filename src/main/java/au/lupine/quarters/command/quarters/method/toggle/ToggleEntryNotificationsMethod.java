package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleEntryNotificationsMethod extends CommandMethod {

    public ToggleEntryNotificationsMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.entrynotifications");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();
        boolean hasEntryNotifications = rmm.hasEntryNotifications(resident);

        rmm.setHasEntryNotifications(resident, !hasEntryNotifications);

        if (hasEntryNotifications) {
            QuartersMessaging.sendSuccessMessage(player, "Successfully disabled entry notifications");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Successfully enabled entry notifications");
        }
    }
}
