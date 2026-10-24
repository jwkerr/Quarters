package au.lupine.quarters.listener;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.SelectionRendererManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class QuarterRenderListener implements Listener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Quarters instance = Quarters.getInstance();

        player.getScheduler().runAtFixedRate(instance, task -> {
                    SelectionRendererManager.getInstance().render(player);
                }, () -> {}, 1L, Quarters.getInstance().config().renderer.ticksBetweenOutlineUpdates
        );
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        SelectionRendererManager.getInstance().hide(event.getPlayer());
    }
}
