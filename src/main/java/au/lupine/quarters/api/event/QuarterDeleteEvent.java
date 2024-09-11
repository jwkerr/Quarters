package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuarterDeleteEvent extends QuartersEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Quarter quarter;
    private final CommandSender sender;

    public QuarterDeleteEvent(@NotNull Quarter quarter, @Nullable CommandSender sender) {
        this.quarter = quarter;
        this.sender = sender;
    }

    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    public @Nullable CommandSender getSender() {
        return sender;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
