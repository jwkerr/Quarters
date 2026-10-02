package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.QuarterPrePvpEvent;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.PvpGracePeriodManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.FlagType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.damage.TownyPlayerDamagePlayerEvent;
import com.palmergames.bukkit.towny.event.player.PlayerDeniedBedUseEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.gmail.nossr50.api.PartyAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This listener is to override and allow actions in quarters when the user has the necessary permissions
 */
public class TownyActionListener implements Listener {

    private final boolean isMcmmoPresent;
    public static final Set<Material> VEHICLE_MATERIALS = Set.of(
            Material.ACACIA_BOAT, Material.BAMBOO_RAFT, Material.BIRCH_BOAT, Material.CHERRY_BOAT,
            Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.MANGROVE_BOAT, Material.OAK_BOAT,
            Material.SPRUCE_BOAT, Material.ACACIA_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT, Material.BIRCH_CHEST_BOAT,
            Material.CHERRY_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT,
            Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.MINECART
    );

    public TownyActionListener() {
        isMcmmoPresent = Bukkit.getPluginManager().getPlugin("mcMMO") != null && Bukkit.getPluginManager().isPluginEnabled("mcMMO");
    }

    @EventHandler
    public void onBuild(@NotNull TownyBuildEvent event) {
        parseEvent(event, ActionType.BUILD);
    }

    @EventHandler
    public void onDestroy(@NotNull TownyDestroyEvent event) {
        parseEvent(event, ActionType.DESTROY);
    }

    @EventHandler
    public void onSwitch(@NotNull TownySwitchEvent event) {
        parseEvent(event, ActionType.SWITCH);
    }

    @EventHandler
    public void onItemUse(@NotNull TownyItemuseEvent event) {
        parseEvent(event, ActionType.ITEM_USE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(@NotNull TownyPlayerDamagePlayerEvent event) {
        parseEvent(event);
    }

    @EventHandler
    public void onPlayerDamage(@NotNull PlayerDeathEvent event) {
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

        if (quarter.hasFlag(FlagType.VEHICLES)) handleVehicles(event, quarter);
    }

    // TODO: This may require some cleanup since TownyPlayerDamagePlayerEvent isn't an instance of TownyActionEvent
    public void parseEvent(@NotNull TownyPlayerDamagePlayerEvent event) {
        Quarter quarter = getEventQuarter(event.isInWilderness(), event.getLocation());

        QuarterPrePvpEvent prePvpEvent = new QuarterPrePvpEvent(event, quarter);
        prePvpEvent.callEvent();
        if (prePvpEvent.isCancelled()) {
            String cancelMessage = prePvpEvent.getCancelMessage();
            if (cancelMessage != null) QuartersMessaging.sendErrorMessage(event.getAttackingPlayer(), cancelMessage);
            return;
        }

        if (quarter != null && quarter.hasFlag(FlagType.PVP))
            handlePvpDamage(prePvpEvent);
    }

    public void parseEvent(@NotNull PlayerDeathEvent event) {
        Quarter quarter = getEventQuarter(false, event.getPlayer().getLocation());

        if (quarter == null || !quarter.hasFlag(FlagType.PVP)) return;

        ConfigManager config = Quarters.getInstance().config();
        event.setShouldDropExperience(config.quarters.pvpSettings.expDropsOnDeath);
        if (!config.quarters.pvpSettings.itemsDropsOnDeath) {
            event.setKeepInventory(true);
            event.getDrops().clear(); // Explicitly remove dropped items to prevent duping
        }
    }

    private @Nullable Quarter getEventQuarter(boolean isInWilderness, @NotNull Location location) {
        if (isInWilderness) return null;
        return QuarterManager.getInstance().getQuarter(location);
    }

    private void handleVehicles(@NotNull TownyActionEvent event, @NotNull Quarter quarter) {
        if (!isVehicle(event.getMaterial())) return;

        if (quarter.isEmbassy()) {
            event.setCancelled(false);
            return;
        }

        if (quarter.isPlayerInTown(event.getPlayer())) event.setCancelled(false);
    }

    private boolean isVehicle(@NotNull Material material) {
        return VEHICLE_MATERIALS.contains(material);
    }

    private void handlePvpDamage(@NotNull QuarterPrePvpEvent event) {
        ConfigManager config = Quarters.getInstance().config();
        Player attacker = event.getAttackingPlayer();
        Player victim = event.getVictimPlayer();

        if (victim.hasPermission("quarters.exempt_from_pvp")) {
            if (config.quarters.pvpSettings.showExemptMessage) {
                QuartersMessaging.sendErrorMessage(attacker, "quarters.pvp.exempt");
            }
            event.setCancelled(true);
            return;
        }

        Quarter quarter = event.getQuarter();
        if (quarter != null && PvpGracePeriodManager.getInstance().isProtected(victim, quarter)) {
            event.setCancelled(true);
            return;
        }

        if (quarter != null && PvpGracePeriodManager.getInstance().isProtected(attacker, quarter)) {
            event.setCancelled(true);
            return;
        }

        Town attackerTown = event.getAttackerTown();
        Town victimTown = event.getVictimTown();

        if (attackerTown != null && victimTown != null) {
            if (!config.quarters.pvpSettings.friendlyFireTown && attackerTown.getUUID().equals(victimTown.getUUID())) {
                event.setCancelled(true);
                return;
            }

            if (!config.quarters.pvpSettings.friendlyFireNation) {
                Nation attackerNation = attackerTown.getNationOrNull();
                Nation victimNation = victimTown.getNationOrNull();

                if (attackerNation != null && victimNation != null && attackerNation.getUUID().equals(victimNation.getUUID())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (isMcmmoPresent && !config.quarters.pvpSettings.friendlyFireMcmmoParty && PartyAPI.inSameParty(attacker, victim)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);
    }

    @EventHandler
    public void onPlayerDeniedBedUse(@NotNull PlayerDeniedBedUseEvent event) {
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

        if (!quarter.hasFlag(FlagType.SLEEPING)) return;

        if (quarter.isEmbassy()) {
            event.setCancelled(true);
            return;
        }

        if (quarter.isPlayerInTown(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplodeEvent(@NotNull TownyExplodingBlocksEvent event) {
        List<Block> allowedBlocks = new ArrayList<>();

        if (event.getBlockList() != null) {
            allowedBlocks.addAll(event.getBlockList());
        }

        // TODO: This is a temporary solution to allow explosions. Make it more efficient since calling quarter for each block is expensive
        for (Block block : event.getVanillaBlockList()) {
            Quarter quarter = QuarterManager.getInstance().getQuarter(block);

            if (quarter == null || !quarter.hasFlag(FlagType.EXPLOSIONS)) continue;
            if (!allowedBlocks.contains(block)) allowedBlocks.add(block);
        }

        event.setBlockList(allowedBlocks);
    }

    @EventHandler
    public void onTownyBurn(@NotNull TownyBurnEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getLocation());

        if (quarter != null && quarter.hasFlag(FlagType.FIRE)) {
            event.setCancelled(false);
        }
    }
}
