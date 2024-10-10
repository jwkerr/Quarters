package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import com.google.gson.GsonBuilder;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PreBuildGsonEvent extends QuartersEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final GsonBuilder builder;

    public PreBuildGsonEvent(GsonBuilder builder) {
        super(true);
        this.builder = builder;
    }

    public GsonBuilder getBuilder() {
        return builder;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
