package net.earthmc.quarters.command.quarters.method.toggle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ResidentMetadataManager;
import net.earthmc.quarters.object.base.CommandMethod;
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
