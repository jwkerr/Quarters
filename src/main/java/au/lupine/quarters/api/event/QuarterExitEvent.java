package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player exits a quarter.
 * <p>
 * This event is fired when a player walks out of a quarter, walks from one quarter into another or quits while inside a quarter.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterExitEvent extends QuartersEvent {

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final Quarter nextQuarter;
    private final boolean exitedViaQuit;

    /**
     * Creates a quarter exit event.
     * @param player The player exiting the quarter.
     * @param resident The Towny resident exiting the quarter.
     * @param quarter The quarter being exited.
     * @param nextQuarter The quarter the player is entering next, or null if they are not walking into any new quarter.
     * @param exitedViaQuit Whether this event was fired because the player quit while inside the quarter.
     */
    public QuarterExitEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, @Nullable Quarter nextQuarter, boolean exitedViaQuit) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.nextQuarter = nextQuarter;
        this.exitedViaQuit = exitedViaQuit;
    }

    /**
     * @return The player exiting the quarter.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return The Towny resident exiting the quarter.
     */
    public @NotNull Resident getResident() {
        return resident;
    }

    /**
     * @return The quarter being exited.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The quarter the player is entering next, null if they are not walking into any new quarter.
     */
    public @Nullable Quarter getNextQuarter() {
        return nextQuarter;
    }

    /**
     * @return True if the player exited the quarter via leaving the server. False if they walked out of the quarter.
     */
    public boolean hasExitedViaQuit() {
        return exitedViaQuit;
    }
}
