package au.lupine.quarters.api.event.ownership;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a resident claims a quarter. Cancelling this event prevents the claim from continuing.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterPreClaimEvent extends CancellableQuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Resident resident;
    private final Quarter quarter;
    private final double price;

    /**
     * Creates a pre-claim event.
     * @param player the player claiming the quarter
     * @param resident the Towny resident claiming the quarter
     * @param quarter the quarter to be claimed
     * @param price the sale price that was validated for this claim
     */
    public QuarterPreClaimEvent(@NotNull Player player, @NotNull Resident resident, @NotNull Quarter quarter, double price) {
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
     * @return the validated sale price; zero means the quarter is free
     */
    public double getPrice() {
        return price;
    }
}
