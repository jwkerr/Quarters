package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetEntryNotificationsMethod extends CommandMethod {

    public SetEntryNotificationsMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set.entrynotifications");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        EntryNotificationType type;
        try {
            type = EntryNotificationType.valueOf(getArgOrThrow(0, "No entry notification type provided").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid entry notification type provided");
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager.getInstance().setEntryNotificationType(resident, type);

        QuartersMessaging.sendSuccessMessage(player, "Your entry notification type has been set to: " + type.getCommonName());
    }
}
