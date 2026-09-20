package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This event is fired before a new {@link Quarter quarter} is created. Cancelling this event prevents the {@link Quarter quarter} from being created. Validation of cuboids happens after this.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterPreCreateEvent extends CancellableQuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private List<Cuboid> cuboids;

    /**
     * Creates a quarter pre create event.
     * @param player The {@link Player player} that wants to create the {@link Quarter quarter}.
     * @param resident The {@link Resident resident} that wants to create the {@link Quarter quarter}.
     * @param cuboids The {@link Cuboid cuboids} of the {@link Quarter quarter}.
     */
    public QuarterPreCreateEvent(@NotNull Player player, @NotNull Resident resident, @NotNull List<Cuboid> cuboids) {
        this.player = player;
        this.resident = resident;
        this.cuboids = new ArrayList<>(cuboids);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link Player player} that wants to create the {@link Quarter quarter}.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link Resident resident} that wants to create the {@link Quarter quarter}.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The {@link Cuboid cuboids} that will be used to make the {@link Quarter quarter}.
     * @apiNote This is passed as an unmodifiable list.
     */
    public @NotNull List<Cuboid> getCuboids() {
        return Collections.unmodifiableList(cuboids);
    }

    /**
     * Sets the {@link Cuboid cuboids} used to make the {@link Quarter quarter}.
     * @param cuboids List of {@link Cuboid cuboids} that will be used for the {@link Quarter quarter}.
     * @apiNote Validation of {@link Cuboid cuboids} happens after this event. This means that Quarters will show errors and stop the creation if the {@link Cuboid cuboids} passed are not valid.
     */
    public void setCuboids(@NotNull List<Cuboid> cuboids) {
        this.cuboids = new ArrayList<>(cuboids);
    }
}
