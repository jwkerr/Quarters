package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.DeletePlayerEvent;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * Class to handle clean-up of residents that no longer exist
 */
public class DeletePlayerListener implements Listener {
    @EventHandler
    public void onDeletePlayer(DeletePlayerEvent event) {
        for (Quarter quarter : QuarterUtil.getAllQuarters()) {
            Resident resident = TownyAPI.getInstance().getResident(event.getPlayerUUID());

            if (quarter.getOwner() == resident) {
                quarter.setOwner(null);
                quarter.save();
            }

            List<Resident> trustedList = quarter.getTrustedResidents();
            if (trustedList.contains(resident)) {
                trustedList.remove(resident);
                quarter.setTrustedResidents(trustedList);
                quarter.save();
            }
        }
    }
}
