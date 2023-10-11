package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyActionEventListener implements Listener {
    @EventHandler
    public void onTownyActionEvent(TownyActionEvent event) {
        if (event.isInWilderness())
            return;

        Location location = event.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null)
            return;

        if (!QuartersAPI.getInstance().hasQuarter(town))
            return;

        Quarter quarter = QuartersAPI.getInstance().getQuarter(location);
        if (quarter == null)
            return;

        allowEventIfOwnerOrTrusted(event, quarter);

        if (event instanceof TownyItemuseEvent)
            allowVehiclePlacementIfStation((TownyItemuseEvent) event, quarter);
    }

    private void allowEventIfOwnerOrTrusted(TownyActionEvent event, Quarter quarter) {
        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == quarter.getOwner() || quarter.getTrustedResidents().contains(resident))
            event.setCancelled(false);
    }

    private void allowVehiclePlacementIfStation(TownyItemuseEvent event, Quarter quarter) {
        if (!isVehicle(event.getMaterial()))
            return;

        if (quarter.getType() == QuarterType.STATION)
            event.setCancelled(false);
    }

    private boolean isVehicle(Material material) {
        return material == Material.ACACIA_BOAT ||
                material == Material.BAMBOO_RAFT ||
                material == Material.BIRCH_BOAT ||
                material == Material.CHERRY_BOAT ||
                material == Material.DARK_OAK_BOAT ||
                material == Material.JUNGLE_BOAT ||
                material == Material.MANGROVE_BOAT ||
                material == Material.OAK_BOAT ||
                material == Material.SPRUCE_BOAT ||
                material == Material.ACACIA_CHEST_BOAT ||
                material == Material.BAMBOO_CHEST_RAFT ||
                material == Material.BIRCH_CHEST_BOAT ||
                material == Material.CHERRY_CHEST_BOAT ||
                material == Material.DARK_OAK_CHEST_BOAT ||
                material == Material.JUNGLE_CHEST_BOAT ||
                material == Material.MANGROVE_CHEST_BOAT ||
                material == Material.OAK_CHEST_BOAT ||
                material == Material.SPRUCE_CHEST_BOAT ||
                material == Material.MINECART;
    }
}
