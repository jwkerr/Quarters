package au.lupine.quarters.listener;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.ParticleManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the notifications that may appear when entering a quarter
 */
public class QuarterEntryListener implements Listener {

    private static final Map<Player, Optional<Quarter>> QUARTER_PLAYER_IS_IN = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Location to = event.getTo();

        Quarter quarter = QuarterManager.getInstance().getQuarter(to);

        Optional<Quarter> previousQuarter = QUARTER_PLAYER_IS_IN.get(player);
        if (quarter != null && (previousQuarter.isEmpty() || !previousQuarter.get().equals(quarter))) onQuarterEntry(quarter, resident);

        QUARTER_PLAYER_IS_IN.put(player, Optional.ofNullable(quarter));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // This listener is to prevent an alert when connecting inside a quarter
        Player player = event.getPlayer();
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Quarter quarter = QuarterManager.getInstance().getQuarter(player.getLocation());

        QUARTER_PLAYER_IS_IN.put(player, Optional.ofNullable(quarter));
    }

    private void onQuarterEntry(Quarter quarter, Resident resident) {
        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        if (rmm.hasEntryNotifications(resident) && ConfigManager.areEntryNotificationsAllowed()) {
            TextComponent.Builder builder = Component.text();
            builder.append(Component.text("You have entered a quarter named ", NamedTextColor.GRAY));
            builder.append(Component.text(quarter.getName(), NamedTextColor.GRAY, TextDecoration.ITALIC).clickEvent(ClickEvent.runCommand("/quarters:q here " + quarter.getUUID())));
            if (quarter.hasOwner()) builder.append(Component.text(" owned by ", NamedTextColor.GRAY).append(
                    ConfigManager.getFormattedName(quarter.getOwner(), Component.text(quarter.getTown().getName()))) // Town name is better than some error if it doesn't work
            );

            QuartersMessaging.sendMessage(resident.getPlayer(), builder.build());
        }

        if (rmm.hasEntryBlinking(resident) && ConfigManager.isEntryParticleBlinkingAllowed())
            ParticleManager.getInstance().drawParticlesAtQuarter(quarter, resident);
    }
}
