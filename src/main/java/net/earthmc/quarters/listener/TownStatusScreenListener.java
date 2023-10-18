package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.object.QuartersTown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Adds a [Quarters] component to town status screens
 */
public class TownStatusScreenListener implements Listener {
    @EventHandler
    public void onTownStatusScreen(TownStatusScreenEvent event) {
        Component hoverComponent = Component.empty();

        int iteration = 0;
        for (QuarterType quarterType : QuarterType.values()) {
            if (iteration != 0)
                hoverComponent = hoverComponent.append(Component.text("\n"));

            int numOfQuarterType = 0;

            QuartersTown quartersTown = new QuartersTown(event.getTown());
            if (quartersTown.hasQuarter()) {
                for (Quarter quarter : quartersTown.getQuarters()) {
                    if (quarter.getType().equals(quarterType))
                        numOfQuarterType++;
                }
            }

            hoverComponent = hoverComponent.append(Component.text(quarterType.getFormattedName() + ": ", NamedTextColor.DARK_GREEN));
            hoverComponent = hoverComponent.append(Component.text(numOfQuarterType, NamedTextColor.GREEN));

            iteration++;
        }

        Component component = Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Quarters", NamedTextColor.GREEN))
                .append(Component.text("]", NamedTextColor.GRAY))
                .hoverEvent(hoverComponent);

        event.getStatusScreen().addComponentOf("quarters_status", component);
    }
}
