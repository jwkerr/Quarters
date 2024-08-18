package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleConstantOutlinesMethod extends CommandMethod {

    public ToggleConstantOutlinesMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.toggle.constantoutlines");
    }

    @Override
    public void execute() {
        Player player = getSenderAsPlayerOrThrow();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();
        boolean hasConstantOutlines = rmm.hasConstantOutlines(resident);

        rmm.setHasConstantOutlines(resident, !hasConstantOutlines);

        if (hasConstantOutlines) {
            QuartersMessaging.sendSuccessMessage(player, "Successfully disabled constant particle outlines");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Successfully enabled constant particle outlines");
        }
    }
}
