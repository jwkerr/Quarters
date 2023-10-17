package net.earthmc.quarters.listener;

import net.earthmc.quarters.object.QuartersPlayer;
import net.earthmc.quarters.task.OutlineParticleTask;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemHeldListener implements Listener {
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null)
            return;

        QuartersPlayer quartersPlayer = new QuartersPlayer(player);
        if (!QuarterUtil.shouldRenderOutlines(quartersPlayer, item.getType()))
            return;

        OutlineParticleTask.createParticlesIfSelectionExists(player);
        OutlineParticleTask.createParticlesIfQuartersExist(player);
    }
}
