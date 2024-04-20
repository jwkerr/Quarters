package net.earthmc.quarters.listener;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.task.OutlineParticleTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    final Quarters plugin;

    public PlayerJoinListener(final Quarters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("particles.enabled"))
            OutlineParticleTask.startTask(event.getPlayer(), plugin);
    }
}
