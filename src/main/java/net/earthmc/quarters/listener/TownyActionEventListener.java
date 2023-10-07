package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.QuartersAPI;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class TownyActionEventListener implements Listener {
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

    private void parseEvent(TownyActionEvent event) {
        if (event.isInWilderness())
            return;

        Location location = event.getLocation();
        Town town = TownyAPI.getInstance().getTown(location);
        if (town == null)
            return;

        if (!QuartersAPI.getInstance().hasQuarter(town))
            return;

        Quarter quarterAtLocation = QuartersAPI.getInstance().getQuarter(location);
        if (quarterAtLocation == null)
            return;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);
        for (Quarter quarter : quarterList) {
            if (quarter.getUUID() == quarterAtLocation.getUUID()) {
                Resident resident = TownyAPI.getInstance().getResident(event.getPlayer());
                if (resident == quarter.getOwner() || quarter.getTrustedResidents().contains(resident)) {
                    event.setCancelled(false);
                }
            }
        }
    }
}
