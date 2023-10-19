package net.earthmc.quarters.listener;

import com.palmergames.bukkit.towny.event.DeletePlayerEvent;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersTown;
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
            if (quarter.getOwner() != null && quarter.getOwner().equals(uuid)) {
                quarter.setOwner(null);
                quarter.setClaimedAt(null);

                QuartersTown quartersTown = new QuartersTown(quarter.getTown());
                if (quartersTown.shouldSellOnDelete() && quarter.getPrice() == null) {
                    Double price = quartersTown.getDefaultSellPrice();
                    if (price != null) {
                        quarter.setPrice(price);
                    } else {
                        quarter.setPrice(0.0);
                    }
                }

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
