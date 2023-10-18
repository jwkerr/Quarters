package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersTown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Class to remove ownership of quarters that are not embassies on town leave
 */
public class TownRemoveResidentListener implements Listener {
    @EventHandler
    public void onTownRemoveResident(TownRemoveResidentEvent event) {
        QuartersTown quartersTown = new QuartersTown(event.getTown());
        if (!quartersTown.hasQuarter())
            return;

        Resident resident = event.getResident();
        for (Quarter quarter : quartersTown.getQuarters()) {
            if (quarter.getOwner().equals(resident) && !quarter.isEmbassy()) {
                quarter.setOwner(null);
                quarter.setClaimedAt(null);
                quarter.save();
            }
        }
    }
}
