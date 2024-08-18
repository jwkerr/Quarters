package net.earthmc.quarters.listener;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.ParticleManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class QuarterParticleListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!ConfigManager.areParticlesEnabled()) return;

        Player player = event.getPlayer();
        Quarters instance = Quarters.getInstance();

        player.getScheduler().runAtFixedRate(instance, task -> {
            if (!QuarterManager.getInstance().shouldRenderOutlinesForPlayer(player)) return;

            ParticleManager pm = ParticleManager.getInstance();
            pm.drawParticlesAtCurrentSelection(player);
            pm.drawParticlesAtQuarters(player);
            }, () -> {}, 1L, ConfigManager.getTicksBetweenParticleOutlines()
        );
    }
}
