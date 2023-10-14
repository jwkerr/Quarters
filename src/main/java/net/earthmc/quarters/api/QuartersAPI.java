package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.TownMetadataManager;
import net.earthmc.quarters.object.*;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class QuartersAPI {
    private static QuartersAPI instance;
    static TownyAPI townyAPI = TownyAPI.getInstance();

    public static QuartersAPI getInstance() {
        if (instance == null)
            instance = new QuartersAPI();

        return instance;
    }

    /**
     * Gets the default wand item's material
     * This can be overridden by player choice
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
        Town town = townyAPI.getTown(location);
        if (town == null)
            return null;

        QuartersTown quartersTown = getQuartersTown(town);
        if (!quartersTown.hasQuarter())
            return null;

        List<Quarter> quarterList = TownMetadataManager.getQuarterListOfTown(town);
        if (quarterList == null)
            return null;

        for (Quarter quarter : quarterList) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            if (QuarterUtil.isLocationInsideCuboidBounds(location, new Cuboid(pos1, pos2)))
                return quarter;
        }

        return null;
    }

    public QuartersPlayer getQuartersPlayer(Resident resident) {
        return new QuartersPlayer(resident);
    }

    public QuartersPlayer getQuartersPlayer(Player player) {
        if (player == null)
            return null;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            return null;

        return new QuartersPlayer(resident);
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
        List<Quarter> quarterList = new ArrayList<>();

        for (Town town : TownyAPI.getInstance().getTowns()) {
            List<Quarter> currentTownQuarterList = getQuartersTown(town).getQuarters();
            if (currentTownQuarterList != null) {
                quarterList.addAll(currentTownQuarterList);
            }
        }

        return quarterList;
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
        QuartersPlayer quartersPlayer = getQuartersPlayer(resident);
        if (!quartersPlayer.isInQuarter())
            return false;

        Quarter quarter = getQuarter(player.getLocation());
        assert quarter != null;
        if (quarter.getType() != QuarterType.SHOP)
            return false;

        return quarter.getOwner() == resident ||
                quarter.getTrustedResidents().contains(resident) ||
                PlayerCacheUtil.getCachePermission(player, location, material, actionType);
    }
}
