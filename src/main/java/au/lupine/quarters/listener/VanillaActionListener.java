package au.lupine.quarters.listener;

import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.FlagType;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.palmergames.bukkit.towny.event.MobRemovalEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class VanillaActionListener implements Listener {

    // Towny uses NORMAL priority with ignore cancelled, so we can't cancel before Towny
    // We need to cancel or accept after Towny has processed the event
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPreCreatureSpawn(@NotNull PreCreatureSpawnEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getSpawnLocation());

        if (quarter == null || !quarter.hasFlag(FlagType.MOBS)) return;
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onCreatureSpawn(@NotNull CreatureSpawnEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getLocation());

        if (quarter == null || !quarter.hasFlag(FlagType.MOBS)) return;
        event.setCancelled(false);
    }

    @EventHandler
    public void onTownyMobRemoval(@NotNull MobRemovalEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getEntity().getLocation());
        if (quarter == null) return;

        // Don't allow Towny to remove mobs if the mobs flag is enabled
        if (quarter.hasFlag(FlagType.MOBS)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(@NotNull BlockFormEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getBlock());

        if (quarter == null) return;
        Material newType = event.getNewState().getType();

        // Prevent snow from forming
        if (newType == Material.SNOW && !quarter.hasFlag(FlagType.SNOW)) {
            event.setCancelled(true);
            return;
        }

        // Prevent water from freezing into ice
        if (newType == Material.ICE && !quarter.hasFlag(FlagType.ICE)) {
            event.setCancelled(true);
        }
    }
}
