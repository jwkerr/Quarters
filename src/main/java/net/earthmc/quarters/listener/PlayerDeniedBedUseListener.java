package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.player.PlayerDeniedBedUseEvent;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.QuarterType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Class to allow usage of beds within quarters
 */
public class PlayerDeniedBedUseListener implements Listener {

    @EventHandler
    public void onPlayerDeniedBedUse(PlayerDeniedBedUseEvent event) {
        Quarter quarter = QuarterManager.getInstance().getQuarter(event.getLocation());

        if (quarter == null) return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null) return;

        if (quarter.getType() == QuarterType.INN) { // TODO: fix this, changed type
            if (quarter.isEmbassy()) {
                event.setCancelled(true);
                return;
            } else {
                if (resident.getTownOrNull() == quarter.getTown()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (quarter.isResidentOwner(resident) || quarter.getTrustedResidents().contains(resident))
            event.setCancelled(true);
    }
}
