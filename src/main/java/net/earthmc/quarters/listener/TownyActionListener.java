package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Class to override Towny's cancellation of certain tasks when the player has permission in a quarter
 */
public class TownyActionListener implements Listener {
    @EventHandler
    public void onBuild(TownyBuildEvent event) {
        parseEvent(event);
    }

    @EventHandler
    public void onDestroy(TownyDestroyEvent event) {
        parseEvent(event);
    }

    @EventHandler
    public void onSwitch(TownySwitchEvent event) {
        parseEvent(event);
    }

    @EventHandler
    public void onItemUse(TownyItemuseEvent event) {
        parseEvent(event);
    }

    public void parseEvent(TownyActionEvent event) {
        if (event.isInWilderness())
            return;

        Location location = event.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null)
            return;

        Quarter quarter = QuarterUtil.getQuarter(location);
        if (quarter == null)
            return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null)
            return;

        if (Objects.equals(quarter.getOwnerResident(), resident) || quarter.getTrustedResidents().contains(resident)) {
            event.setCancelled(false);
            return;
        }

        switch (quarter.getType()) {
            case COMMONS:
                if (!(event instanceof TownySwitchEvent || event instanceof TownyItemuseEvent))
                    return;

                handleEvent(event, quarter, resident);
                break;
            case STATION:
                if (!(event instanceof TownyItemuseEvent || event instanceof TownyDestroyEvent || event instanceof TownySwitchEvent))
                    return;

                // Extra tolerance on Y coordinate to check for boats placed in the lowest quadrant of a quarter
                handleStation(event, QuarterUtil.getQuarter(location.add(0, 0.25, 0)), resident);
                break;
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
