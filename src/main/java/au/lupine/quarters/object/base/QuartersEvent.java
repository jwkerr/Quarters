package au.lupine.quarters.object.base;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class QuartersEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public QuartersEvent() {
        super();
    }

    public QuartersEvent(boolean isAsync) {
        super(isAsync);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
