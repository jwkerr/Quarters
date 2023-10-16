package net.earthmc.quarters.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandUtil {
    public static boolean isPlayerInQuarter(Player player) {
        QuartersPlayer quartersPlayer = QuartersAPI.getInstance().getQuartersPlayer(player);

        if (!quartersPlayer.isInQuarter()) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
            return false;
        }

        return true;
    }

    public static boolean isSelectionValid(Player player, Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null) {
            QuartersMessaging.sendErrorMessage(player, "You must select two valid positions using the Quarters wand, or by using /quarters {pos1/pos2}");
            return false;
        }

        return true;
    }

    public static boolean isQuarterInPlayerTown(Player player, Quarter quarter) {
        if (quarter.getTown() != TownyAPI.getInstance().getTown(player)) {
            QuartersMessaging.sendErrorMessage(player, "This quarter is not part of your town");
            return false;
        }

        return true;
    }

    public static boolean hasPermissionOrMayor(Player player, String permission) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return false;

        if (!(player.hasPermission(permission) || resident.isMayor())) {
            QuartersMessaging.sendErrorMessage(player, "You do not have permission to perform this action");
            return false;
        }

        return true;
    }
}
