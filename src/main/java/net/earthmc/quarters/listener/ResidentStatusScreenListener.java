package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.ResidentStatusScreenEvent;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * Adds a Quarters: n section to resident status screens
 */
public class ResidentStatusScreenListener implements Listener {

    @EventHandler
    public void onResidentStatusScreen(ResidentStatusScreenEvent event) {
        QuarterManager qm = QuarterManager.getInstance();
        Player player = event.getResident().getPlayer();
        if (player == null) return;

        List<Quarter> quarters = qm.getQuarters(player);
        if (quarters.isEmpty()) return;

        Component component = Component.empty()
                .append(Component.text("Quarters: ", NamedTextColor.DARK_GREEN))
                .append(Component.text(quarters.size(), NamedTextColor.GREEN));

        event.getStatusScreen().addComponentOf("quarters_resident_status", component);
    }
}
