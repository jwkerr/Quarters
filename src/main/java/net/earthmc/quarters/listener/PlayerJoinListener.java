package net.earthmc.quarters.listener;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.object.task.OutlineParticleTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!ConfigManager.areParticlesEnabled()) return;

        OutlineParticleTask.startTask(event.getPlayer(), Quarters.getInstance());
    }
}
