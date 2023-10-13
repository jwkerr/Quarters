package net.earthmc.quarters.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.entity.Player;

public class CommandUtil {
    public static boolean isPlayerInQuarter(Player player, Quarter quarter) {
        if (!QuartersAPI.getInstance().isPlayerInQuarter(player) || quarter == null) {
            QuartersMessaging.sendErrorMessage(player, "You are not standing within a quarter");
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
