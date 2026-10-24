package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.base.CommandMethod;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ToggleConstantOutlinesMethod extends CommandMethod {

    public ToggleConstantOutlinesMethod() {
        super("constantoutlines", "quarters.command.quarters.toggle.constantoutlines");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        Player player = getSenderAsPlayerOrThrow(source);

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();
        boolean hasConstantOutlines = rmm.hasConstantOutlines(resident);

        rmm.setHasConstantOutlines(resident, !hasConstantOutlines);

        if (hasConstantOutlines) {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.constantoutlines.feedback.disabled");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.toggle.constantoutlines.feedback.enabled");
        }
    }
}
