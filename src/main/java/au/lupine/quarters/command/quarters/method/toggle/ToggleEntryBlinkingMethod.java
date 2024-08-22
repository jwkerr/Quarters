package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleEntryBlinkingMethod extends CommandMethod {

    public ToggleEntryBlinkingMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.entryblinking");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();
        boolean hasEntryBlinking = rmm.hasEntryBlinking(resident);

        rmm.setHasEntryBlinking(resident, !hasEntryBlinking);

        if (hasEntryBlinking) {
            QuartersMessaging.sendSuccessMessage(player, "Successfully disabled entry blinking");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Successfully enabled entry blinking");
        }
    }
}
