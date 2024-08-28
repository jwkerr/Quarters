package au.lupine.quarters.listener;

import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.event.plot.changeowner.PlotPreClaimEvent;
import com.palmergames.bukkit.towny.event.resident.NewResidentEvent;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

/**
 * This class listens to events to clean up and maintain the integrity of quarters' internal state
 */
public class QuarterIntegrityListener implements Listener {

    @EventHandler
    public void onTownRemoveResident(TownRemoveResidentEvent event) {
        Town town = event.getTown();
        Resident resident = event.getResident();

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            if (quarter.isResidentOwner(resident) && !quarter.isEmbassy()) {
                quarter.setOwner(null); // Remove their ownership if it's not an embassy
                quarter.save();
            }
        }
    }

    @EventHandler
    public void onTownUnclaimTownBlock(TownUnclaimEvent event) {
        Town town = event.getTown();
        if (town == null) return;

        for (Quarter quarter : QuarterManager.getInstance().getQuarters(event.getWorldCoord())) {
            quarter.delete(); // The quarter now contains wilderness and should not exist
        }
    }

    @EventHandler
    public void onNewResident(NewResidentEvent event) {
        UUID uuid = event.getResident().getUUID();

        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            boolean wasChanged = false;

            if (quarter.isOwner(uuid)) {
                quarter.setOwner(null);
                wasChanged = true;
            }

            List<UUID> trusted = quarter.getTrusted();
            if (trusted.remove(uuid)) {
                quarter.setTrusted(trusted);
                wasChanged = true;
            }

            if (wasChanged) quarter.save();
        }
    }

    @EventHandler
    public void onPlotPurchasedByPlayer(PlotPreClaimEvent event) {
        Town town = event.getTownBlock().getTownOrNull();
        if (town == null) return;

        if (!QuarterManager.getInstance().hasQuarter(event.getTownBlock())) return;

        Resident resident = event.getNewResident();
        if (resident == null) return;

        TownyMessaging.sendErrorMsg(
                resident.getPlayer(),
                "The plot you are claiming contains regions from the plugin Quarters that may allow others to alter the plot"
        );
    }
}
