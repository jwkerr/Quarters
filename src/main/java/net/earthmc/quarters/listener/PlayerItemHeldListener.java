package net.earthmc.quarters.listener;

import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.task.OutlineParticleTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Class to render particles when item is switched before waiting for next OutlineParticleTask tick
 */
public class PlayerItemHeldListener implements Listener {

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) { // TODO: move particles enabled config check to here
        Player player = event.getPlayer();

        if (!QuarterManager.getInstance().shouldRenderOutlines(player)) return;

        OutlineParticleTask.createParticlesIfSelectionExists(player);
        OutlineParticleTask.createParticlesIfQuartersExist(player);
    }
}
