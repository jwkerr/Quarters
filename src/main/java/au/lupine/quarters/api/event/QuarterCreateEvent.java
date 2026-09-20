package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a quarter is created. This cannot be cancelled and only acts as a read-only event. Useful for logging purposes or to modify the quarter after it is created.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterCreateEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Quarter quarter;

    /**
     * Creates a quarter create event.
     * @param quarter The quarter being created.
     */
    public QuarterCreateEvent(@NotNull Quarter quarter) {
        this.quarter = quarter;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link Quarter quarter} that was created.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }
}
