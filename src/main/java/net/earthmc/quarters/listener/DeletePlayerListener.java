package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.DeletePlayerEvent;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
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

        for (Quarter quarter : QuarterManager.getInstance().getAllQuarters()) {
            UUID owner = quarter.getOwner();
            if (uuid.equals(owner)) {
                quarter.setOwner(null);
                quarter.save();
            }

            List<UUID> trustedList = quarter.getTrusted();
            if (trustedList.contains(uuid)) {
                trustedList.remove(uuid);
                quarter.setTrusted(trustedList);
                quarter.save();
            }
        }
    }
}
