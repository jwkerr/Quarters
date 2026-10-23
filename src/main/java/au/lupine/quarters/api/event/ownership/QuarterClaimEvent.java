package au.lupine.quarters.api.event.ownership;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a resident has claimed a quarter. This event cannot be cancelled.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterClaimEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final double price;

    /**
     * Creates a claim event.
     * @param player the player who claimed the quarter
     * @param resident the Towny resident who claimed the quarter
     * @param quarter the claimed quarter
     * @param price the sale price paid; zero means the quarter was free
     */
    public QuarterClaimEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, double price) {
        this.player = player;
        this.resident = resident;
        this.quarter = quarter;
        this.price = price;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Resident getResident() {
        return resident;
    }

    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return the sale price paid; zero means the quarter was free
     */
    public double getPrice() {
        return price;
    }
}
