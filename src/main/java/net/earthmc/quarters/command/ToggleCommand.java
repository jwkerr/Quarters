package net.earthmc.quarters.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.manager.ResidentMetadataManager;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.CommandUtil;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;

@CommandAlias("quarters|q")
public class ToggleCommand extends BaseCommand {
    @Subcommand("toggle")
    @Description("Toggle quarters settings")
    @CommandPermission("quarters.command.quarters.toggle")
    @CommandCompletion("constantoutlines|embassy|sellondelete")
    public void onToggle(Player player, String arg) {
        switch (arg) {
            case "constantoutlines":
                toggleConstantOutlines(player);
                break;
            case "embassy":
                setQuarterAtLocationEmbassyStatus(player);
                break;
            case "sellondelete":
                toggleSellOnDelete(player);
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

        boolean hasConstantOutlines = ResidentMetadataManager.hasConstantOutlines(resident);

        ResidentMetadataManager.setConstantOutlines(resident, !hasConstantOutlines);
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

    private void toggleSellOnDelete(Player player) {
        if (!CommandUtil.hasPermissionOrMayor(player, "quarters.action.sellondelete"))
            return;

        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null)
            return;

        boolean shouldSellOnDelete = TownMetadataManager.shouldSellOnDelete(town);

        TownMetadataManager.setSellOnDelete(town, !shouldSellOnDelete);

        if (!shouldSellOnDelete) {
            QuartersMessaging.sendSuccessMessage(player, "Quarters in " + town.getName() + " will now automatically go for sale when the owner is deleted by Towny");
        } else {
            QuartersMessaging.sendSuccessMessage(player, "Quarters in " + town.getName() + " will no longer automatically go for sale when the owner is deleted by Towny");
        }
    }

    public static void toggleQuarterEmbassyStatus(Player player, Quarter quarter) {
        quarter.setEmbassy(!quarter.isEmbassy());

        Resident resident = quarter.getOwnerResident();
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
