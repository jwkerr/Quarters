package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.ResidentStatusScreenEvent;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterType;
import net.earthmc.quarters.object.QuartersPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ResidentStatusScreenListener implements Listener {
    @EventHandler
    public void onResidentStatusScreen(ResidentStatusScreenEvent event) {
        Component hoverComponent = Component.empty();

        QuartersPlayer quartersPlayer = new QuartersPlayer(event.getResident().getPlayer());
        List<Quarter> quarterList = quartersPlayer.getQuartersOwnedByPlayer();

        int iteration = 0;
        for (QuarterType quarterType : QuarterType.values()) {
            if (iteration != 0)
                hoverComponent = hoverComponent.append(Component.text("\n"));

            int numOfQuarterType = 0;

            for (Quarter quarter : quarterList) {
                if (quarter.getType().equals(quarterType))
                    numOfQuarterType++;
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

        event.getStatusScreen().addComponentOf("quarters_resident_status", component);
    }
}
