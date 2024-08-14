package net.earthmc.quarters.listener.newlisteners;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.api.manager.QuarterManager;
import net.earthmc.quarters.object.entity.Quarter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class QuarterEntryNotificationListener implements Listener {

    private static final Map<Player, Quarter> QUARTER_PLAYER_IS_IN = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!ConfigManager.areEntryNotificationsAllowed()) return;

        Player player = event.getPlayer();
        Location to = event.getTo();

        Quarter quarter = QuarterManager.getInstance().getQuarter(to);

        Quarter previousQuarter = QUARTER_PLAYER_IS_IN.get(player);
        if (quarter != null && (previousQuarter == null || !previousQuarter.equals(quarter))) sendQuarterEntryAlert(quarter, player);

        QUARTER_PLAYER_IS_IN.put(player, quarter);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // This listener is to prevent an alert when connecting inside a quarter
        if (!ConfigManager.areEntryNotificationsAllowed()) return;
        
        Player player = event.getPlayer();
        Quarter quarter = QuarterManager.getInstance().getQuarter(player.getLocation());

        QUARTER_PLAYER_IS_IN.put(player, quarter);
    }

    private void sendQuarterEntryAlert(Quarter quarter, Player player) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("You have entered a quarter named " + quarter.getName(), NamedTextColor.GRAY));
        if (quarter.hasOwner()) builder.append(Component.text(" owned by ", NamedTextColor.GRAY).append(
                ConfigManager.getFormattedName(quarter.getOwner(), Component.text(quarter.getTown().getName()))) // Town name is better than some error if it doesn't work
        );

        QuartersMessaging.sendMessage(player, builder.build());
    }
}
