package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.DeletePlayerEvent;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.util.QuarterUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class to handle clean-up of residents that no longer exist
 */
public class DeletePlayerListener implements Listener {
    @EventHandler
    public void onDeletePlayer(DeletePlayerEvent event) {
        UUID uuid = event.getPlayerUUID();

        for (Quarter quarter : QuarterUtil.getAllQuarters()) {
            if (Objects.equals(quarter.getOwner(), uuid)) {
                quarter.setOwner(null);
                quarter.setClaimedAt(null);
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
