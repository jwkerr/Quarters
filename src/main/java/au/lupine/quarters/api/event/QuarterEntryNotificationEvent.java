package au.lupine.quarters.api.event;

import au.lupine.quarters.object.entity.Quarter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuarterEntryNotificationEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Quarter quarter;
    private List<Component> messageComponents;

    public QuarterEntryNotificationEvent(@NotNull Player player, @NotNull Quarter quarter, @NotNull List<Component> messageComponents) {
        this.player = player;
        this.quarter = quarter;
        this.messageComponents = messageComponents;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    public List<Component> getMessageComponents() {
        return messageComponents;
    }

    public void setMessageComponents(@NotNull List<Component> messageComponents) {
        this.messageComponents = messageComponents;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
