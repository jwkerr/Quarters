package net.earthmc.quarters.api.manager;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class QuarterManager {

    private static QuarterManager instance;

    private QuarterManager() {}

    public static QuarterManager getInstance() {
        if (instance == null) instance = new QuarterManager();
        return instance;
    }

    public @Nullable Quarter getQuarter(Location location) {
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
     * @return The quarter with the specified UUID or null if it does not exist
     */
    public @Nullable Quarter getQuarter(UUID uuid) {
        for (Quarter quarter : getAllQuarters()) {
            if (quarter.getUUID().equals(uuid)) return quarter;
        }

        return null;
    }

    public List<Quarter> getAllQuarters() {
        List<Quarter> quarterList = new ArrayList<>();

        for (Town town : TownyAPI.getInstance().getTowns()) {
            List<Quarter> currentTownQuarterList = getQuarters(town);
            quarterList.addAll(currentTownQuarterList);
        }

        return quarterList;
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
     * @return True if the player owns at least one quarter
     */
    public boolean hasQuarter(@NotNull Player player) {
        return !getQuarters(player).isEmpty();
    }

    /**
     * @return A list of all quarters owned by the specified player
     */
    public List<Quarter> getQuarters(@NotNull Player player) {
        List<Quarter> quarterList = new ArrayList<>();

        for (Quarter quarter : getAllQuarters()) {
            if (player.getUniqueId().equals(quarter.getOwner())) quarterList.add(quarter);
        }

        return quarterList;
    }

    /**
     * @return True if the specified player is inside a quarter
     */
    public boolean isPlayerInQuarter(Player player) {
        Location location = player.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null) return false;

        List<Quarter> quarterList = getQuarters(town);
        if (quarterList.isEmpty()) return false;

        for (Quarter quarter : quarterList) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (cuboid.isLocationInsideBounds(location)) return true;
            }
        }

        return false;
    }

    public boolean shouldRenderOutlines(Player player) {
        if (!ConfigManager.areParticlesEnabled()) return false;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return false;

        if (ResidentMetadataManager.getInstance().hasConstantOutlines(resident) && ConfigManager.areConstantParticleOutlinesAllowed())
            return true;

        return player.getInventory().getItemInMainHand().getType().equals(ConfigManager.getWandMaterial());
    }
}
