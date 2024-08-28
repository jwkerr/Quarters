package au.lupine.quarters.api.manager;

import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class QuarterManager {

    private static QuarterManager instance;

    private QuarterManager() {}

    public static QuarterManager getInstance() {
        if (instance == null) instance = new QuarterManager();
        return instance;
    }

    /**
     * @return The quarter at the specified location or null if there is none
     */
    public @Nullable Quarter getQuarter(@NotNull Location location) {
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null) return null;

        List<Quarter> quarters = getQuarters(town);
        if (quarters.isEmpty()) return null;

        for (Quarter quarter : quarters) {
            if (quarter.isLocationInsideBounds(location)) return quarter;
        }

        return null;
    }

    /**
     * @return The quarter at the specified block or null if there is none
     */
    public @Nullable Quarter getQuarter(@NotNull Block block) {
        return getQuarter(block.getLocation());
    }

    /**
     * @return The quarter with the specified UUID or null if it does not exist
     */
    public @Nullable Quarter getQuarter(UUID uuid) {
        for (Quarter quarter : getAllQuarters()) {
            if (quarter.getUUID().equals(uuid)) return quarter;
        }

        return null;
    }

    /**
     * @return Every single currently registered quarter
     */
    public List<Quarter> getAllQuarters() {
        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        for (Town town : TownyAPI.getInstance().getTowns()) {
            List<Quarter> currentTownQuarterList = getQuarters(town);
            quarters.addAll(currentTownQuarterList);
        }

        return quarters;
    }

    /**
     * @return True if there is at least one quarter defined within the specified town
     */
    public boolean hasQuarter(@NotNull Town town) {
        return !getQuarters(town).isEmpty();
    }

    /**
     * @return A list of all quarters in the specified town
     */
    public List<Quarter> getQuarters(@NotNull Town town) {
        return TownMetadataManager.getInstance().getQuarterList(town);
    }

    /**
     * Used to add or remove quarters from a town
     */
    public void setQuarters(@NotNull Town town, List<Quarter> quarters) {
        TownMetadataManager.getInstance().setQuarterList(town, quarters);
    }

    /**
     * @return True if there is at least one quarter defined within the specified nation
     */
    public boolean hasQuarter(@NotNull Nation nation) {
        return !getQuarters(nation).isEmpty();
    }

    /**
     * @return A list of all quarters in the specified nation
     */
    public List<Quarter> getQuarters(@NotNull Nation nation) {
        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        for (Town town : nation.getTowns()) {
            quarters.addAll(getQuarters(town));
        }

        return quarters;
    }

    /**
     * @return True if the player owns at least one quarter
     */
    public boolean hasQuarter(@NotNull OfflinePlayer player) {
        return !getQuarters(player).isEmpty();
    }

    /**
     * @return A list of all quarters owned by the specified player
     */
    public List<Quarter> getQuarters(@NotNull OfflinePlayer player) {
        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        for (Quarter quarter : getAllQuarters()) {
            if (player.getUniqueId().equals(quarter.getOwner())) quarters.add(quarter);
        }

        return quarters;
    }

    /**
     * @return True if there is a quarter that intersects with this townblock
     */
    public boolean hasQuarter(@NotNull TownBlock townBlock) {
        return !getQuarters(townBlock).isEmpty();
    }

    /**
     * @return Every quarter that intersects with this townblock
     */
    public List<Quarter> getQuarters(@NotNull TownBlock townBlock) {
        return getQuarters(townBlock.getWorldCoord());
    }

    /**
     * @return True if there is a quarter that intersects with this worldcoord
     */
    public boolean hasQuarter(@NotNull WorldCoord worldCoord) {
        return !getQuarters(worldCoord).isEmpty();
    }

    /**
     * @return Every quarter that intersects with this worldcoord
     */
    public List<Quarter> getQuarters(@NotNull WorldCoord worldCoord) {
        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        Town town = worldCoord.getTownOrNull();
        if (town == null) return quarters;

        BoundingBox wcBounding = worldCoord.getBoundingBox();
        for (Quarter quarter : getQuarters(town)) {
            if (quarter.intersectsWith(wcBounding)) quarters.add(quarter);
        }

        return quarters;
    }

    /**
     * @return True if the specified player is inside a quarter
     */
    public boolean isPlayerInQuarter(Player player) {
        Location location = player.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null) return false;

        List<Quarter> quarters = getQuarters(town);
        if (quarters.isEmpty()) return false;

        for (Quarter quarter : quarters) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (cuboid.isLocationInsideBounds(location)) return true;
            }
        }

        return false;
    }

    /**
     * This will measure distance using {@link Quarter#getDistanceFrom(Location)}
     * @param location The location to find quarters near
     * @param radius The maximum radius to get quarters in
     * @return Every quarter in the specified area within that radius
     */
    public List<Quarter> getQuartersNear(@NotNull Location location, double radius) {
        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        for (Quarter quarter : getAllQuarters()) {
            if (quarter.getDistanceFrom(location) <= radius) quarters.add(quarter);
        }

        return quarters;
    }

    /**
     * @param location The location you want to sort from, if this position is not in a town the result will always be an empty list
     * @return All quarters in the town at the specified location sorted from lowest to highest distance
     */
    public List<Quarter> getQuartersInTownSortedByDistance(@NotNull Location location) {
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null) return new CopyOnWriteArrayList<>();

        return getQuarters(town).stream()
                .sorted(Comparator.comparingDouble(quarter -> quarter.getDistanceFrom(location)))
                .toList();
    }

    /**
     * @return True if particles should be drawn for this player given the server config, the player's own settings and the item they are holding
     */
    public boolean shouldRenderOutlinesForPlayer(Player player) {
        if (!ConfigManager.areParticlesEnabled()) return false;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return false;

        if (ResidentMetadataManager.getInstance().hasConstantOutlines(resident) && ConfigManager.areConstantParticleOutlinesAllowed()) return true;

        return player.getInventory().getItemInMainHand().getType().equals(ConfigManager.getWandMaterial());
    }
}
