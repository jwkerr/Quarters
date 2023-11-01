package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.player.PlayerDeniedBedUseEvent;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Class to allow usage of beds within quarters
 */
public class PlayerDeniedBedUseListener implements Listener {
    @EventHandler
    public void onPlayerDeniedBedUse(PlayerDeniedBedUseEvent event) {
        Quarter quarter = QuarterUtil.getQuarter(event.getLocation());

        if (quarter == null)
            return;

        Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
        if (resident == null)
            return;

        if (quarter.getType() == QuarterType.COMMONS) {
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

        if (Objects.equals(quarter.getOwnerResident(), resident) || quarter.getTrustedResidents().contains(resident))
            event.setCancelled(true);
    }
}
