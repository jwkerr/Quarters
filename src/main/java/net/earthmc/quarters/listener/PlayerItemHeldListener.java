package net.earthmc.quarters.listener;

import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.task.OutlineParticleTask;
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

        if (item.getType() != Quarters.WAND)
            return;

        OutlineParticleTask.createParticlesIfSelectionExists(player);
        OutlineParticleTask.createParticlesIfQuartersExist(player);
    }
}
