package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.ParticleManager;
import au.lupine.quarters.api.manager.QuarterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class QuarterParticleListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Quarters instance = Quarters.getInstance();

        player.getScheduler().runAtFixedRate(instance, task -> {
            if (!QuarterManager.getInstance().shouldRenderOutlinesForPlayer(player)) return;

            ParticleManager pm = ParticleManager.getInstance();
            pm.drawParticlesAtCurrentSelection(player);
            pm.drawParticlesAtAllQuarters(player);
            }, () -> {}, 1L, ConfigManager.getTicksBetweenParticleOutlines()
        );
    }
}
