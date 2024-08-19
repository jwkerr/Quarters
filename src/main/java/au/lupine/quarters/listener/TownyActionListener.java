package au.lupine.quarters.listener;

import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.QuarterType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.player.PlayerDeniedBedUseEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This listener is to override and allow actions in quarters when the user has the necessary permissions
 */
public class TownyActionListener implements Listener {

    public static final Set<Material> VEHICLE_MATERIALS = Set.of(
            Material.ACACIA_BOAT, Material.BAMBOO_RAFT, Material.BIRCH_BOAT, Material.CHERRY_BOAT,
            Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.MANGROVE_BOAT, Material.OAK_BOAT,
            Material.SPRUCE_BOAT, Material.ACACIA_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT, Material.BIRCH_CHEST_BOAT,
            Material.CHERRY_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT,
            Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.MINECART
    );

    @EventHandler
    public void onBuild(TownyBuildEvent event) {
        parseEvent(event, ActionType.BUILD);
    }

    @EventHandler
    public void onDestroy(TownyDestroyEvent event) {
        parseEvent(event, ActionType.DESTROY);
    }

    @EventHandler
    public void onSwitch(TownySwitchEvent event) {
        parseEvent(event, ActionType.SWITCH);
    }

    @EventHandler
    public void onItemUse(TownyItemuseEvent event) {
        parseEvent(event, ActionType.ITEM_USE);
    }

    public void parseEvent(@NotNull TownyActionEvent event, @NotNull ActionType type) {
        if (event.isInWilderness()) return;

        Location location = event.getLocation();

        Quarter quarter = QuarterManager.getInstance().getQuarter(location);
        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null) return;

        if (quarter.testPermission(type, resident)) {
            event.setCancelled(false);
            return;
        }

        if (quarter.getType().equals(QuarterType.STATION)) handleStation(event, quarter);
    }

    private void handleStation(TownyActionEvent event, Quarter quarter) {
        if (!isVehicle(event.getMaterial())) return;

        if (quarter.isEmbassy()) {
            event.setCancelled(false);
            return;
        }

        if (quarter.isPlayerInTown(event.getPlayer())) event.setCancelled(false);
    }

    private boolean isVehicle(Material material) {
        return VEHICLE_MATERIALS.contains(material);
    }

    @EventHandler
    public void onPlayerDeniedBedUse(PlayerDeniedBedUseEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getLocation());
        if (quarter == null) return;

        Player player = event.getPlayer();
        if (player == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        if (quarter.isResidentOwner(resident) || quarter.getTrustedResidents().contains(resident)) {
            event.setCancelled(true);
            return;
        }

        if (!quarter.getType().equals(QuarterType.INN)) return;

        if (quarter.isEmbassy()) {
            event.setCancelled(true);
            return;
        }

        if (quarter.isPlayerInTown(player)) event.setCancelled(true);
    }
}
