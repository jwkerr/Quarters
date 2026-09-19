package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Called before Quarters creates its shared Gson instance.
 * <p>
 * This can be used to register additional type adapters or configure the builder used by Quarters.
 * @author Fruitloopins
 * @since 2.0.0
 */
public class QuartersPreBuildGsonEvent extends QuartersEvent {

    private final GsonBuilder builder;

    /**
     * Creates a pre-build Gson event.
     * @param builder The Gson builder that will be used to create Quarters' shared Gson instance.
     */
    public QuartersPreBuildGsonEvent(@NotNull GsonBuilder builder) {
        super(true);
        this.builder = builder;
    }

    /**
     * @return The mutable Gson builder that will be used to create Quarters' shared Gson instance.
     */
    public @NotNull GsonBuilder getBuilder() {
        return builder;
    }
}
