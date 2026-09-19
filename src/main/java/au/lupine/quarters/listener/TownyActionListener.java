package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.QuarterType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.damage.TownyPlayerDamagePlayerEvent;
import com.palmergames.bukkit.towny.event.player.PlayerDeniedBedUseEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @EventHandler
    public void onPlayerDamage(TownyPlayerDamagePlayerEvent event) {
        parseEvent(event);
    }

    public void parseEvent(@NotNull TownyActionEvent event, @NotNull ActionType type) {
        Quarter quarter = getEventQuarter(event.isInWilderness(), event.getLocation());
        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null) return;

        if (quarter.testPermission(type, resident)) {
            event.setCancelled(false);
            return;
        }

        if (quarter.getType().equals(QuarterType.STATION)) handleStation(event, quarter);
    }

    // TODO: This may require some cleanup since TownyPlayerDamagePlayerEvent isn't an instance of TownyActionEvent
    public void parseEvent(@NotNull TownyPlayerDamagePlayerEvent event) {
        Quarter quarter = getEventQuarter(event.isInWilderness(), event.getLocation());
        if (quarter == null) return;

        if (quarter.getType().equals(QuarterType.ARENA)) handleArenaDamage(event, quarter);
    }

    private @Nullable Quarter getEventQuarter(boolean isInWilderness, @NotNull Location location) {
        if (isInWilderness) return null;
        return QuarterManager.getInstance().getQuarter(location);
    }

    private void handleStation(@NotNull TownyActionEvent event, @NotNull Quarter quarter) {
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

    private void handleArenaDamage(@NotNull TownyPlayerDamagePlayerEvent event, @NotNull Quarter quarter) {
        ConfigManager config = Quarters.getInstance().config();
        if (!config.quarters.arenaQuarter.enabled) return;

        // Explicitly allow the damage first
        event.setCancelled(false);

        Town attackerTown = TownyAPI.getInstance().getTown(event.getAttackingPlayer());
        Town victimTown = TownyAPI.getInstance().getTown(event.getVictimPlayer());

        if (attackerTown != null && victimTown != null) {
            if (!config.quarters.arenaQuarter.friendlyFireTown && attackerTown.getUUID().equals(victimTown.getUUID())) {
                event.setCancelled(true);
                return;
            }

            if (!config.quarters.arenaQuarter.friendlyFireNation) {
                Nation attackerNation = attackerTown.getNationOrNull();
                Nation victimNation = victimTown.getNationOrNull();

                if (attackerNation == null || victimNation == null) return;

                if (attackerNation.getUUID().equals(victimNation.getUUID())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
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
