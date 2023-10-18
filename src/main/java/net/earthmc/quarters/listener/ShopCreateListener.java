package net.earthmc.quarters.listener;

import com.ghostchu.quickshop.api.event.ShopCreateEvent;
import com.ghostchu.quickshop.api.event.ShopDeleteEvent;
import com.palmergames.bukkit.towny.object.TownyPermission;
import net.earthmc.quarters.api.QuartersAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * QuickShops compatibility
 */
public class ShopCreateListener implements Listener {
    @EventHandler
    public void onShopCreate(ShopCreateEvent event) {
        Player player = event.getCreator().getBukkitPlayer().orElse(null);
        if (player == null)
            return;

        Location location = event.getShop().getLocation();
        Material material = Material.CHEST;
        TownyPermission.ActionType actionType = TownyPermission.ActionType.BUILD;

        if (QuartersAPI.getInstance().canPlayerEditShopAtLocation(player, location, material, actionType))
            event.setCancelled(false);
    }

    @EventHandler
    public void onShopDelete(ShopDeleteEvent event) {

    }
}
