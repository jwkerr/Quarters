package net.earthmc.quarters.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.TextComponent;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.event.statusscreen.ResidentStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.QuarterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class listens for Towny status screen events to add info about Quarters to them
 */
public class StatusScreenListener implements Listener {

    @EventHandler
    public void onResidentStatusScreen(ResidentStatusScreenEvent event) {
        QuarterManager qm = QuarterManager.getInstance();
        Player player = event.getResident().getPlayer();
        if (player == null) return;

        List<Quarter> quarters = qm.getQuarters(player);
        if (quarters.isEmpty()) return;

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("Quarters: ", NamedTextColor.DARK_GREEN));
        builder.append(Component.text(quarters.size(), NamedTextColor.GREEN));

        event.getStatusScreen().addComponentOf("Quarters_resident_quarter_count", builder.build());
    }

    @EventHandler
    public void onTownStatusScreen(TownStatusScreenEvent event) {
        Town town = event.getTown();

        Map<QuarterType, Integer> numOfTypes = new HashMap<>();
        for (Quarter quarter : QuarterManager.getInstance().getQuarters(town)) {
            QuarterType type = quarter.getType();
            int num = numOfTypes.getOrDefault(type, 0);

            numOfTypes.put(type, num + 1);
        }

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("[", NamedTextColor.GRAY));
        builder.append(Component.text("Quarters", NamedTextColor.GREEN));
        builder.append(Component.text("]", NamedTextColor.GRAY));

        TextComponent.Builder hoverBuilder = Component.text();
        for (Map.Entry<QuarterType, Integer> entry : numOfTypes.entrySet()) {
            hoverBuilder.append(Component.text(entry.getKey().getCommonName() + ": ", NamedTextColor.DARK_GREEN));
            hoverBuilder.append(Component.text(entry.getValue(), NamedTextColor.GREEN));
        }

        builder.hoverEvent(hoverBuilder.build());

        event.getStatusScreen().addComponentOf("Quarters_town_quarter_counts", builder.build());
    }
}
