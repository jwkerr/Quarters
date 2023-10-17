package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.object.QuartersTown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

        QuartersTown quartersTown = QuartersAPI.getInstance().getQuartersTown(town);
        if (!quartersTown.hasQuarter())
            return;

        allowEventIfOwnerOrTrusted(event, QuartersAPI.getInstance().getQuarter(location));

        // Extra tolerance on Y coordinate to check for boats placed in the lowest quadrant of a quarter
        allowVehicleActionIfStation(event, QuartersAPI.getInstance().getQuarter(location.add(0, 0.25, 0)));
    }

    private void allowEventIfOwnerOrTrusted(TownyActionEvent event, Quarter quarter) {
        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == quarter.getOwner() || quarter.getTrustedResidents().contains(resident))
            event.setCancelled(false);
    }

    private void allowVehicleActionIfStation(TownyActionEvent event, Quarter quarter) {
        if (!(event instanceof TownyItemuseEvent || event instanceof TownyDestroyEvent || event instanceof TownySwitchEvent))
            return;

        if (!isVehicle(event.getMaterial()))
            return;

        if (!(quarter.getType() == QuarterType.STATION))
            return;

        if (quarter.isEmbassy()) { // We use embassy status to represent that this station is for anyone's use
            event.setCancelled(false);
            return;
        }

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null)
            return;

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
