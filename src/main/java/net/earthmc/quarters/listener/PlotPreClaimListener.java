package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.plot.changeowner.PlotPreClaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersTown;
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
        if (town == null)
            return;

        QuartersTown quartersTown = new QuartersTown(town);
        if (!quartersTown.hasQuarter())
            return;

        WorldCoord worldCoord = event.getTownBlock().getWorldCoord();
        Location lowerMostCorner = worldCoord.getLowerMostCornerLocation();
        Location upperMostCorner = worldCoord.getUpperMostCornerLocation();

        Cuboid worldCoordCuboid = new Cuboid(lowerMostCorner, upperMostCorner);

        for (Quarter quarter : quartersTown.getQuarters()) {
            for (Cuboid cuboid : quarter.getCuboids()) {
                if (worldCoordCuboid.doesIntersectWith(cuboid)) {
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
