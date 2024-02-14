package net.earthmc.quarters.api;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.*;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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

    /**
     * Gets a quarter by its UUID
     *
     * @param uuid UUID to resolve a quarter from
     * @return The quarter with the specified UUID or null if it does not exist
     */
    @Nullable
    public Quarter getQuarter(UUID uuid) {
        for (Quarter quarter : getAllQuarters()) {
            if (quarter.getUUID().equals(uuid)) return quarter;
        }

        return null;
    }

    /**
     * Gets Quarters' "player" instance with Quarters specific methods
     *
     * @param player Player to resolve QuartersPlayer from
     * @return The specified player's QuartersPlayer instance
     */
    public QuartersPlayer getQuartersPlayer(Player player) {
        return new QuartersPlayer(player);
    }

    /**
     * Gets Quarters' "player" instance with Quarters specific methods
     *
     * @param resident Resident to resolve QuartersPlayer from
     * @return The specified player's QuartersPlayer instance
     */
    public QuartersPlayer getQuartersPlayer(Resident resident) {
        return new QuartersPlayer(resident);
    }

    /**
     * Gets Quarters' "town" instance with Quarters specific methods
     *
     * @param town Town to resolve QuartersTown from
     * @return The specified town's QuartersTown instance
     */
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
}
