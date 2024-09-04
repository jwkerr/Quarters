package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuarterDeleteEvent extends CancellableQuartersEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Quarter quarter;
    private final Cause cause;
    private final CommandSender sender;

    public QuarterDeleteEvent(@NotNull Quarter quarter, @NotNull Cause cause, @Nullable CommandSender sender) {
        this.quarter = quarter;
        this.cause = cause;
        this.sender = sender;
    }

    /**
     * @return The deleted quarter.
     */
    @NotNull
    public Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The {@link Cause cause} of the quarter's deletion.
     */
    @NotNull
    public Cause getCause() {
        return cause;
    }

    /**
     * @return The {@link CommandSender sender} who caused the deletion.
     */
    @Nullable
    public CommandSender getSender() {
        return sender;
    }

    public enum Cause {
        UNKNOWN,
        /**
         * The quarter was deleted for containing Wilderness.
         */
        CONTAINS_WILDERNESS,
        /**
         * The quarter was deleted because a player used /quarters delete.
         */
        COMMAND,
        /**
         * The quarter was deleted because an admin used /quartersadmin delete.
         */
        ADMIN_COMMAND;

        @ApiStatus.Internal
        public boolean ignoresCancellation() {
            return this == CONTAINS_WILDERNESS;
        }
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
