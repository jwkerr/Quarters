package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.*;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class QuartersAPI {
    private static QuartersAPI instance;

    public static QuartersAPI getInstance() {
        if (instance == null)
            instance = new QuartersAPI();

        return instance;
    }

    /**
     * Gets the wand item's material
     *
     * @return Material of the configured Quarters wand
     */
    public Material getWand() {
        return Quarters.WAND;
    }

    /**
     * Gets a quarter at a specific location
     *
     * @param location Location to check
     * @return The quarter at the specified location or null if there is none
     */
    @Nullable
    public Quarter getQuarter(Location location) {
        return QuarterUtil.getQuarter(location);
    }

    public QuartersPlayer getQuartersPlayer(Player player) {
        return new QuartersPlayer(player);
    }

    public QuartersTown getQuartersTown(Town town) {
        return new QuartersTown(town);
    }

    /**
     * Gets all the quarters within the server
     *
     * @return A list of all quarters within the server
     */
    public List<Quarter> getAllQuarters() {
        return QuarterUtil.getAllQuarters();
    }

    /**
     * Check if a player can edit a shop at a location
     *
     * @param player Player to check the permissions of
     * @param location Block location to check permissions at
     * @param material Block material being placed/edited
     * @param actionType Towny action type. Build, destroy etc.
     * @return True if the player can build there
     */
    public boolean canPlayerEditShopAtLocation(Player player, Location location, Material material, TownyPermission.ActionType actionType) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        QuartersPlayer quartersPlayer = new QuartersPlayer(player);
        if (!quartersPlayer.isInQuarter())
            return false;

        Quarter quarter = QuarterUtil.getQuarter(player.getLocation());
        assert quarter != null;
        if (quarter.getType() != QuarterType.SHOP)
            return false;

        return quarter.getOwnerResident() == resident ||
                quarter.getTrustedResidents().contains(resident) ||
                PlayerCacheUtil.getCachePermission(player, location, material, actionType);
    }
}
