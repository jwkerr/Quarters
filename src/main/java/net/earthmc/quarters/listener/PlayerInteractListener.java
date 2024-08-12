package net.earthmc.quarters.listener;

import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.SelectionManager;
import net.earthmc.quarters.object.state.SelectionType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class to handle detection of clicks with the wand item
 */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getMaterial() != ConfigManager.getWandMaterial()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("quarters.wand")) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Location location = block.getLocation();

        event.setCancelled(true);

        SelectionType type = event.getAction().isLeftClick() ? SelectionType.LEFT : SelectionType.RIGHT;
        SelectionManager.getInstance().selectPosition(player, location, type);

        // TODO: reimplement chat message
    }
}
