package au.lupine.quarters.listener;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.ParticleManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.SelectionManager;
import au.lupine.quarters.object.state.SelectionType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Class to handle detection of actions with the wand item
 */
public class QuartersWandListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getMaterial().equals(ConfigManager.getWandMaterial())) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("quarters.wand")) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();

        event.setCancelled(true);

        SelectionType type = event.getAction().isLeftClick() ? SelectionType.LEFT : SelectionType.RIGHT;

        SelectionManager sm = SelectionManager.getInstance();
        sm.selectPosition(player, location, type);

        QuartersMessaging.sendMessage(player, sm.getSelectedPositionComponent(type, location));
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        if (!ConfigManager.areParticlesEnabled()) return;

        Player player = event.getPlayer();

        if (!QuarterManager.getInstance().shouldRenderOutlinesForPlayer(player)) return;

        ParticleManager pm = ParticleManager.getInstance(); // Draw outlines on item hold to make them more snappy
        pm.drawParticlesAtCurrentSelection(player);
        pm.drawParticlesAtAllQuarters(player);
    }
}
