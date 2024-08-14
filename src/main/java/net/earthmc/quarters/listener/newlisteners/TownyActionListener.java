package net.earthmc.quarters.listener.newlisteners;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.ActionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class TownyActionListener implements Listener {

    @EventHandler
    public void onBuild(TownyBuildEvent event) {
        parseEvent(event, ActionType.BUILD);
    }

    @EventHandler
    public void onDestroy(TownyDestroyEvent event) {
        parseEvent(event, ActionType.DESTROY);
    }

    @EventHandler
    public void onSwitch(TownySwitchEvent event) {
        parseEvent(event, ActionType.SWITCH);
    }

    @EventHandler
    public void onItemUse(TownyItemuseEvent event) {
        parseEvent(event, ActionType.ITEM_USE);
    }

    public void parseEvent(@NotNull TownyActionEvent event, @NotNull ActionType type) {
        if (event.isInWilderness()) return;

        Location location = event.getLocation();

        Quarter quarter = QuarterManager.getInstance().getQuarter(location);
        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null) return;

        if (quarter.isResidentOwner(resident) || quarter.getTrustedResidents().contains(resident)) {
            event.setCancelled(false);
            return;
        }

        if (quarter.testPermission(type, resident)) {
            event.setCancelled(false);
            return;
        }
    }

    private void handleStation(TownyActionEvent event, Quarter quarter, Resident resident) {
        if (!isVehicle(event.getMaterial()))
            return;

        handleEvent(event, quarter, resident);
    }
    
    private void handleEvent(TownyActionEvent event, Quarter quarter, Resident resident) {
        if (quarter.isEmbassy()) { // We use embassy status to represent that certain quarter types are for anyone's use
            event.setCancelled(false);
            return;
        }

        if (quarter.getTown() == resident.getTownOrNull())
            event.setCancelled(false);
    }

    private boolean isVehicle(Material material) {
        return material == Material.ACACIA_BOAT || material == Material.BAMBOO_RAFT ||
                material == Material.BIRCH_BOAT || material == Material.CHERRY_BOAT ||
                material == Material.DARK_OAK_BOAT || material == Material.JUNGLE_BOAT ||
                material == Material.MANGROVE_BOAT || material == Material.OAK_BOAT ||
                material == Material.SPRUCE_BOAT || material == Material.ACACIA_CHEST_BOAT ||
                material == Material.BAMBOO_CHEST_RAFT || material == Material.BIRCH_CHEST_BOAT ||
                material == Material.CHERRY_CHEST_BOAT || material == Material.DARK_OAK_CHEST_BOAT ||
                material == Material.JUNGLE_CHEST_BOAT || material == Material.MANGROVE_CHEST_BOAT ||
                material == Material.OAK_CHEST_BOAT || material == Material.SPRUCE_CHEST_BOAT ||
                material == Material.MINECART;
    }
}
