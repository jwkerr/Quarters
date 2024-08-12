package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Class to remove ownership of quarters that are not embassies on town leave
 */
public class TownRemoveResidentListener implements Listener {

    @EventHandler
    public void onTownRemoveResident(TownRemoveResidentEvent event) {
        Town town = event.getTown();
        QuarterManager qm = QuarterManager.getInstance();

        if (!qm.hasQuarter(town)) return;

        Resident resident = event.getResident();
        for (Quarter quarter : qm.getQuarters(town)) {
            if (resident.equals(quarter.getOwnerResident()) && !quarter.isEmbassy()) {
                quarter.setOwner(null);
                quarter.save();
            }
        }
    }
}
