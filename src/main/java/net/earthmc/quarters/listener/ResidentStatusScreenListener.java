package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.ResidentStatusScreenEvent;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuartersPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ResidentStatusScreenListener implements Listener {
    @EventHandler
    public void onResidentStatusScreen(ResidentStatusScreenEvent event) {
        QuartersPlayer quartersPlayer = new QuartersPlayer(event.getResident());

        List<Quarter> quarterList = quartersPlayer.getQuartersOwnedByPlayer();
        if (quarterList.isEmpty())
            return;

        Component component = Component.empty()
                .append(Component.text("Quarters: ", NamedTextColor.DARK_GREEN))
                .append(Component.text(quarterList.size(), NamedTextColor.GREEN));

        event.getStatusScreen().addComponentOf("quarters_resident_status", component);
    }
}
