package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player enters a quarter.
 * <p>
 * This event is fired when a player walks into a quarter, walks from one quarter into another or joins while inside a quarter.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterEnterEvent extends QuartersEvent {

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final Quarter previousQuarter;
    private final boolean enteredViaJoin;

    /**
     * Creates a quarter enter event.
     * @param player The player entering the quarter.
     * @param resident The Towny resident entering the quarter.
     * @param quarter The quarter being entered.
     * @param previousQuarter The quarter the player came from, or null if they were not previously in a quarter.
     * @param enteredViaJoin Whether this event was fired because the player joined while inside the quarter.
     */
    public QuarterEnterEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @Nullable Quarter previousQuarter, boolean enteredViaJoin) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.previousQuarter = previousQuarter;
        this.enteredViaJoin = enteredViaJoin;
    }

    /**
     * @return The player entering the quarter.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The Towny resident entering the quarter.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The quarter being entered.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The quarter the player came from, null if they were not previously in a quarter.
     */
    public @Nullable Quarter getPreviousQuarter() {
        return previousQuarter;
    }

    /**
     * @return True if the player entered the quarter via joining the server. False if they walked into the quarter.
     */
    public boolean hasEnteredViaJoin() {
        return enteredViaJoin;
    }
}
