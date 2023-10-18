package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.DeletePlayerEvent;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

/**
 * Class to handle clean-up of residents that no longer exist
 */
public class DeletePlayerListener implements Listener {
    @EventHandler
    public void onDeletePlayer(DeletePlayerEvent event) {
        UUID uuid = event.getPlayerUUID();

        for (Quarter quarter : QuarterUtil.getAllQuarters()) {
            if (quarter.getOwnerUUID() != null && quarter.getOwnerUUID().equals(uuid)) {
                quarter.setOwnerUUID(null);
                quarter.setClaimedAt(null);
                quarter.save();
            }

            List<UUID> trustedList = quarter.getTrustedResidentsUUIDs();
            if (trustedList.contains(uuid)) {
                Quarters.INSTANCE.getLogger().info("here");
                trustedList.remove(uuid);
                quarter.setTrustedResidentsUUIDs(trustedList);
                quarter.save();
            }
        }
    }
}
