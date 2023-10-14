package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.ResidentMetadataManager;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class QuartersPlayer {
    Resident resident;
    Player player;
    Boolean constantOutlines;
    Material customWand;

    public QuartersPlayer(Resident resident) {
        this.resident = resident;
        this.player = resident.getPlayer();
        this.constantOutlines = ResidentMetadataManager.hasConstantOutlines(resident);
        this.customWand = ResidentMetadataManager.getCustomWand(resident);
    }

    public Resident getResident() {
        return this.resident;
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets a boolean representing whether the player has constant quarter outlines enabled or not
     *
     * @return True if they have constant outlines enabled
     */
    public Boolean hasConstantOutlines() {
        return this.constantOutlines;
    }

    /**
     * Gets the material of the player's custom wand
     *
     * @return The player's configured custom wand or null
     */
    public Material getCustomWand() {
        return this.customWand;
    }

    /**
     * Gets a boolean representing if the player is inside a quarter or not
     *
     * @return True if the player is inside a quarter
     */
    public boolean isInQuarter() {
        Location location = player.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null)
            return false;

        QuartersTown quartersTown = new QuartersTown(town);
        if (!quartersTown.hasQuarter())
            return false;

        List<Quarter> quarterList = quartersTown.getQuarters();
        if (quarterList == null)
            return false;

        for (Quarter quarter : quarterList) {
            Location pos1 = quarter.getPos1();
            Location pos2 = quarter.getPos2();

            if (QuarterUtil.isLocationInsideCuboidBounds(player.getLocation(), new Cuboid(pos1, pos2)))
                return true;
        }

        return false;
    }
}
