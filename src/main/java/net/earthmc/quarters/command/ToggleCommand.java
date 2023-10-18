package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.ResidentMetadataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class ToggleCommand extends BaseCommand {
    @Subcommand("toggle")
    @Description("Toggle quarters settings")
    @CommandPermission("quarters.command.quarters.toggle")
    @CommandCompletion("constantoutlines|embassy")
    public void onToggle(Player player, String arg) {
        switch (arg) {
            case "constantoutlines":
                toggleConstantOutlines(player);
                break;
            case "embassy":
                setQuarterAtLocationEmbassyStatus(player);
                break;
            default:
                QuartersMessaging.sendErrorMessage(player, "Invalid argument");
        }
    }

    private void toggleConstantOutlines(Player player) {
        if (!CommandUtil.hasPermission(player, "quarters.action.constantoutlines"))
            return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return;

        Boolean hasConstantOutlines = ResidentMetadataManager.hasConstantOutlines(resident);
        if (hasConstantOutlines == null) {
            hasConstantOutlines = true;
        } else {
            hasConstantOutlines = !hasConstantOutlines;
        }

        ResidentMetadataManager.setConstantOutlines(resident, hasConstantOutlines);
    }

    private void setQuarterAtLocationEmbassyStatus(Player player) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.embassy"))
            return;

        if (!CommandUtil.isPlayerInQuarter(player))
            return;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;

        if (!CommandUtil.isQuarterInPlayerTown(player, quarter))
            return;

        toggleQuarterEmbassyStatus(player, quarter);
    }

    public static void toggleQuarterEmbassyStatus(Player player, Quarter quarter) {
        quarter.setEmbassy(!quarter.isEmbassy());

        Resident resident = quarter.getOwner();
        if (resident != null && resident.getTownOrNull() != quarter.getTown())
            quarter.setOwner(null);

        quarter.save();

        if (quarter.isEmbassy()) {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is now an embassy");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "This quarter is no longer an embassy");
        }
    }
}
