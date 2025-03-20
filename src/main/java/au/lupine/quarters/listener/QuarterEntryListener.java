package au.lupine.quarters.listener;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.event.QuarterEntryNotificationEvent;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the notifications that may appear when entering a quarter
 */
public class QuarterEntryListener implements Listener {

    private static final Map<UUID, Optional<Quarter>> QUARTER_PLAYER_IS_IN = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Location to = event.getTo();

        Quarter quarter = QuarterManager.getInstance().getQuarter(to);

        Optional<Quarter> previousQuarter = QUARTER_PLAYER_IS_IN.getOrDefault(player.getUniqueId(), Optional.empty());
        if (quarter != null && (previousQuarter.isEmpty() || !previousQuarter.get().equals(quarter)))
            onQuarterEntry(quarter, resident);

        QUARTER_PLAYER_IS_IN.put(player.getUniqueId(), Optional.ofNullable(quarter));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // This listener is to prevent an alert when connecting inside a quarter
        Player player = event.getPlayer();
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Quarter quarter = QuarterManager.getInstance().getQuarter(player.getLocation());

        QUARTER_PLAYER_IS_IN.put(player.getUniqueId(), Optional.ofNullable(quarter));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QUARTER_PLAYER_IS_IN.remove(event.getPlayer().getUniqueId());
    }

    private void onQuarterEntry(Quarter quarter, Resident resident) {
        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        if (rmm.hasEntryNotifications(resident) && ConfigManager.areEntryNotificationsAllowed())
            sendEntryNotification(quarter, resident);

        if (rmm.hasEntryBlinking(resident) && ConfigManager.isEntryParticleBlinkingAllowed())
            quarter.blinkForResident(resident);
    }

    private void sendEntryNotification(Quarter quarter, Resident resident) {
        List<Component> components = new ArrayList<>();

        Component name = Component.text(quarter.getName(), TextColor.color(quarter.getColour().getRGB())).clickEvent(ClickEvent.runCommand("/quarters:q here " + quarter.getUUID()));
        Component owner = quarter.hasOwner() ? ConfigManager.getFormattedName(quarter.getOwner(), null) : Component.text("Unowned", NamedTextColor.GRAY);
        Component type = Component.text(quarter.getType().getCommonName(), NamedTextColor.GRAY);

        components.add(name);
        components.add(owner);
        components.add(type);

        if (quarter.isForSale()) {
            Component price = QuartersMessaging.OPEN_SQUARE_BRACKET
                    .append(Component.text(TownyEconomyHandler.getFormattedBalance(quarter.getPrice()), NamedTextColor.GRAY))
                    .append(QuartersMessaging.CLOSED_SQUARE_BRACKET)
                    .hoverEvent(Component.text("Click to claim!", NamedTextColor.GRAY))
                    .clickEvent(ClickEvent.runCommand("/quarters:q claim " + quarter.getUUID()));

            components.add(price);
        }

        Player player = resident.getPlayer();
        if (player == null) return;

        QuarterEntryNotificationEvent event = new QuarterEntryNotificationEvent(player, quarter, components);
        event.callEvent();
        components = event.getMessageComponents();

        JoinConfiguration jc = JoinConfiguration.separator(Component.text(" - ", TextColor.color(QuartersMessaging.PLUGIN_COLOUR.getRGB())));
        Component notification = Component.join(jc, components);

        EntryNotificationType notificationType = ResidentMetadataManager.getInstance().getEntryNotificationType(resident);

        switch (notificationType) {
            case ACTION_BAR -> player.sendActionBar(notification);
            case CHAT -> QuartersMessaging.sendMessage(player, notification);
        }
    }
}
