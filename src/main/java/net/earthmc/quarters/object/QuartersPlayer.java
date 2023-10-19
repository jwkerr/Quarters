package net.earthmc.quarters.object;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.ResidentMetadataManager;
import net.earthmc.quarters.manager.SelectionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuartersPlayer {
    private final Resident resident;
    private final Player player;
    private final Boolean constantOutlines;

    public QuartersPlayer(Player player) {
        this.resident = TownyAPI.getInstance().getResident(player);
        this.player = player;
        this.constantOutlines = ResidentMetadataManager.hasConstantOutlines(resident);
    }

    /**
     * Gets the player's selected area, selected using the wand item or commands
     *
     * @return The player's current {@link Selection}
     */
    public Selection getSelection() {
        return SelectionManager.selectionMap.computeIfAbsent(player, k -> new Selection());
    }

    /**
     * Gets the player's currently added cuboids, added with /quarters selection add
     *
     * @return The player's currently added cuboid selections
     */
    public List<Cuboid> getCuboids() {
        return SelectionManager.cuboidsMap.computeIfAbsent(player, k -> new ArrayList<>());
    }

    /**
     *
     * @return The player's Towny resident instance
     */
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
        if (this.constantOutlines == null)
            return false;

        return this.constantOutlines;
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
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (cuboid.isLocationInsideBounds(player.getLocation()))
                    return true;
            }
        }

        return false;
    }
}
