package net.earthmc.quarters.listener;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.manager.SelectionManager;
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
        if (event.getMaterial() != Quarters.WAND)
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission("quarters.wand"))
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Location location = block.getLocation();

        event.setCancelled(true);

        if (event.getAction().isLeftClick())
            SelectionManager.selectPosition(player, location, true);

        if (event.getAction().isRightClick())
            SelectionManager.selectPosition(player, location, false);
    }
}
