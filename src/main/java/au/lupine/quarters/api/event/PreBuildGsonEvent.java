package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import com.google.gson.GsonBuilder;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called before {@link au.lupine.quarters.Quarters Quarters} creates its shared {@link GsonBuilder Gson instance}. This can be used to register additional type adapters or configure the builder used by {@link au.lupine.quarters.Quarters Quarters}.
 * @author Fruitloopins
 * @since 1.0.5
 * @deprecated Since 2.0.0. Use {@link QuartersPreBuildGsonEvent} instead. This was renamed to make it clear that it belongs to the Quarters API. The internal logic still works, but the new name is preferred.
 */
@Deprecated(forRemoval = true)
public class PreBuildGsonEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GsonBuilder builder;

    /**
     * Creates a pre-build Gson event.
     *
     * @param builder The Gson builder that will be used to create Quarters' shared Gson instance.
     */
    public PreBuildGsonEvent(@NotNull GsonBuilder builder) {
        super(true);
        this.builder = builder;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The mutable {@link GsonBuilder Gson builder} that will be used to create Quarters' shared Gson instance.
     */
    public @NotNull GsonBuilder getBuilder() {
        return builder;
    }
}
