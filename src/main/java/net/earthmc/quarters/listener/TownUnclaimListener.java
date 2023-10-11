package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.manager.QuarterDataManager;
import net.earthmc.quarters.object.Cuboid;
import net.earthmc.quarters.object.Quarter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class TownUnclaimListener implements Listener {
    @EventHandler
    public void onTownUnclaim(TownUnclaimEvent event) {
        Town town = event.getTown();
        if (town == null)
            return;

        List<Quarter> quarterList = QuarterDataManager.getQuarterListFromTown(town);
        if (quarterList == null)
            return;

        for (Quarter quarter : quarterList) {
            Cuboid cuboid = new Cuboid(quarter.getPos1(), quarter.getPos2());
            for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
                for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                    for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                        Location location = new Location(town.getWorld(), x, y, z);

                        Town currentPosTown = TownyAPI.getInstance().getTown(location);
                        if (currentPosTown == null || currentPosTown != town) {
                            quarter.delete();
                            return;
                        }
                    }
                }
            }
        }
    }
}
