package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.object.Resident;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when a player receives the entry notification when entering a quarter.
 * <p>
 * This event is fired after Quarters has built the notification component, but before the
 * notification is sent to the player.
 * <p>
 * Cancelling this event prevents the notification from being sent.
 *
 * @since 2.0.0
 * @author pernio, galacticwarrior9
 */
public class QuarterEntryNotificationEvent extends CancellableQuartersEvent {

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private List<Component> notifications;
    private EntryNotificationType notificationType;

    /**
     * Creates a quarter entry notification event.
     * @param player The player receiving the notification.
     * @param resident The Towny resident receiving the notification.
     * @param quarter The quarter the player entered.
     * @param notifications The notification components that will be sent before they are merged together into a message.
     * @param notificationType The way the notification will be sent.
     */
    public QuarterEntryNotificationEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @NotNull List<Component> notifications, @NotNull EntryNotificationType notificationType) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.notifications = notifications;
        this.notificationType = notificationType;
    }

    /**
     * @return The player receiving the notification.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The Towny resident receiving the notification.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The quarter the player entered.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The notification components that will be sent before they are merged together into a message.
     */
    public @NotNull List<Component> getNotifications() {
        return notifications;
    }

    /**
     * Sets the notification components that will be sent.
     * @param notification The new notification component.
     */
    public void setNotifications(@NotNull List<Component> notification) {
        this.notifications = notification;
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
