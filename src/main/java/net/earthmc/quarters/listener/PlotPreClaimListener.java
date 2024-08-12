package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.plot.changeowner.PlotPreClaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Cuboid;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Class to warn a player if the plot they are about to claim contains any quarters
 */
public class PlotPreClaimListener implements Listener {

    @EventHandler
    public void onPlotClaim(PlotPreClaimEvent event) {
        Town town = event.getTownBlock().getTownOrNull();
        if (town == null) return;

        QuarterManager qm = QuarterManager.getInstance();
        if (!qm.hasQuarter(town)) return;

        WorldCoord worldCoord = event.getTownBlock().getWorldCoord();
        Location lowerMostCorner = worldCoord.getLowerMostCornerLocation();
        Location upperMostCorner = worldCoord.getUpperMostCornerLocation();

        Cuboid worldCoordCuboid = new Cuboid(lowerMostCorner, upperMostCorner);

        for (Quarter quarter : qm.getQuarters(town)) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (worldCoordCuboid.intersectsWith(cuboid)) {
                    Resident resident = event.getNewResident();
                    if (resident == null)
                        return;

                    TownyMessaging.sendMsg(resident.getPlayer(), "The plot you are claiming contains regions from the plugin Quarters that may allow others to alter the plot");
                    return;
                }
            }
        }
    }
}
