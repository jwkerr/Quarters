package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when a {@link Player player} walks out of a {@link Quarter quarter}, walks from one {@link Quarter quarter} into another or quits while inside a {@link Quarter quarter}.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterExitEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final Quarter nextQuarter;
    private final boolean exitedViaQuit;

    /**
     * Creates a quarter exit event.
     * @param player The {@link Player player} exiting the {@link Quarter quarter}.
     * @param resident The {@link Resident resident} exiting the {@link Quarter quarter}.
     * @param quarter The {@link Quarter quarter} being exited.
     * @param nextQuarter The {@link Quarter quarter} the {@link Player player} is entering next, or null if they are not walking into any new {@link Quarter quarter}.
     * @param exitedViaQuit Whether this event was fired because the {@link Player player} quit while inside the {@link Quarter quarter}.
     */
    public QuarterExitEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @Nullable Quarter nextQuarter, boolean exitedViaQuit) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.nextQuarter = nextQuarter;
        this.exitedViaQuit = exitedViaQuit;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link Player player} exiting the {@link Quarter quarter}.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link Resident resident} exiting the {@link Quarter quarter}.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The {@link Quarter quarter} being exited.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The {@link Quarter quarter} the {@link Player player} is entering next, null if they are not walking into any new {@link Quarter quarter}.
     */
    public @Nullable Quarter getNextQuarter() {
        return nextQuarter;
    }

    /**
     * @return True if the {@link Player player} exited the {@link Quarter quarter} via leaving the server. False if they walked out of the {@link Quarter quarter}.
     */
    public boolean hasExitedViaQuit() {
        return exitedViaQuit;
    }
}
