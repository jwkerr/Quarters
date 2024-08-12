package net.earthmc.quarters.command.quarters.method.toggle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ResidentMetadataManager;
import net.earthmc.quarters.object.base.CommandMethod;
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

        boolean hasConstantOutlines = ResidentMetadataManager.getInstance().hasConstantOutlines(resident);

        ResidentMetadataManager.getInstance().setHasConstantOutlines(resident, !hasConstantOutlines);

        if (hasConstantOutlines) {
            QuartersMessaging.sendSuccessMessage(player, "Successfully disabled constant particle outlines");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Successfully enabled constant particle outlines");
        }
    }
}
