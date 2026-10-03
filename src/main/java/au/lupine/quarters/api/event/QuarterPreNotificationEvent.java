package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.object.Resident;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Called before a {@link Player player} receives the entry notification when entering a {@link Quarter quarter}. Cancelling this event prevents the notification from being sent.
 * @since 2.0.0
 * @author pernio, galacticwarrior9
 */
public class QuarterPreNotificationEvent extends CancellableQuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private List<Component> notifications;
    private EntryNotificationType notificationType;

    /**
     * Creates a {@link Quarter quarter} entry notification event.
     * @param player The {@link Player player} receiving the notification.
     * @param resident The {@link Resident resident} receiving the notification.
     * @param quarter The {@link Quarter quarter} the player entered.
     * @param notifications The notification components that will be sent before they are merged together into a message.
     * @param notificationType The way the notification will be sent.
     */
    public QuarterPreNotificationEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @NotNull List<Component> notifications, @NotNull EntryNotificationType notificationType) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.notifications = new ArrayList<>(notifications);
        this.notificationType = notificationType;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link Player player} receiving the notification.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link Resident resident} receiving the notification.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The {@link Quarter quarter} the {@link Player player} entered.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The notification components that will be sent before they are merged together into a message.
     * @apiNote This is passed as an unmodifiable list.
     */
    public @NotNull List<Component> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    /**
     * Sets the notification components that will be sent.
     * @param notifications The new notification components.
     */
    public void setNotifications(@NotNull List<Component> notifications) {
        this.notifications = new ArrayList<>(notifications);
    }

    /**
     * @return The way the notification will be sent.
     */
    public @NotNull EntryNotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the way the notification will be sent.
     * @param notificationType The new notification type.
     */
    public void setNotificationType(@NotNull EntryNotificationType notificationType) {
        this.notificationType = notificationType;
    }
}
