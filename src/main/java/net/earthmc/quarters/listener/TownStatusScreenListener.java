package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.QuarterType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Adds a [Quarters] component to town status screens
 */
public class TownStatusScreenListener implements Listener {

    @EventHandler
    public void onTownStatusScreen(TownStatusScreenEvent event) {
        Town town = event.getTown();

        QuarterManager qm = QuarterManager.getInstance();
        if (!qm.hasQuarter(town)) return;

        Component hoverComponent = Component.empty();

        int iteration = 0;
        for (QuarterType quarterType : QuarterType.values()) {
            if (iteration != 0)
                hoverComponent = hoverComponent.append(Component.newline());

            int numOfQuarterType = 0;

            for (Quarter quarter : qm.getQuarters(town)) {
                if (quarter.getType().equals(quarterType))
                    numOfQuarterType++;
            }

            hoverComponent = hoverComponent.append(Component.text(quarterType.getCommonName() + ": ", NamedTextColor.DARK_GREEN));
            hoverComponent = hoverComponent.append(Component.text(numOfQuarterType, NamedTextColor.GREEN));

            iteration++;
        }

        Component component = Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Quarters", NamedTextColor.GREEN))
                .append(Component.text("]", NamedTextColor.GRAY))
                .hoverEvent(hoverComponent);

        event.getStatusScreen().addComponentOf("quarters_town_status", component);
    }
}
