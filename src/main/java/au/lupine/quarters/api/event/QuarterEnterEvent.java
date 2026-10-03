package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when a {@link Player player} walks into a {@link Quarter quarter}, walks from one {@link Quarter quarter} into another or joins while inside a {@link Quarter quarter}.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterEnterEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final Quarter previousQuarter;
    private final boolean enteredViaJoin;

    /**
     * Creates a quarter enter event.
     * @param player The {@link Player player} entering the {@link Quarter quarter}.
     * @param resident The {@link Resident resident} entering the {@link Quarter quarter}.
     * @param quarter The {@link Quarter quarter} being entered.
     * @param previousQuarter The {@link Quarter quarter} the {@link Player player} came from, or null if they were not previously in a {@link Quarter quarter}.
     * @param enteredViaJoin Whether this event was fired because the {@link Player player} joined while inside the {@link Quarter quarter}.
     */
    public QuarterEnterEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @Nullable Quarter previousQuarter, boolean enteredViaJoin) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.previousQuarter = previousQuarter;
        this.enteredViaJoin = enteredViaJoin;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link Player player} entering the {@link Quarter quarter}.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link Resident resident} entering the {@link Quarter quarter}.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The {@link Quarter quarter} being entered.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The {@link Quarter quarter} the {@link Player player} came from, null if they were not previously in a {@link Quarter quarter}.
     */
    public @Nullable Quarter getPreviousQuarter() {
        return previousQuarter;
    }

    /**
     * @return True if the {@link Player player} entered the {@link Quarter quarter} via joining the server. False if they walked into the {@link Quarter quarter}.
     */
    public boolean hasEnteredViaJoin() {
        return enteredViaJoin;
    }
}
