package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.QuarterDeleteCause;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired before a new {@link Quarter quarter} is deleted. Cancelling this event prevents the {@link Quarter quarter} from being deleted.
 * @since 2.0.0
 * @author pernio, galacticwarrior9
 */
public class QuarterPreDeleteEvent extends CancellableQuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable CommandSender sender;
    private final @Nullable Player player;
    private final @Nullable Resident resident;
    private final Quarter quarter;
    private final QuarterDeleteCause cause;

    /**
     * Creates a quarter pre deletion event.
     * @param sender The person who caused/requested to delete the {@link Quarter quarter}. Null if no person was involved, e.g. unclaiming the {@link com.palmergames.bukkit.towny.object.TownBlock plot}.
     * @param quarter The quarter being deleted.
     * @param cause The cause of why the quarter is being deleted.
     */
    public QuarterPreDeleteEvent(@Nullable CommandSender sender, @NotNull Quarter quarter, @NotNull QuarterDeleteCause cause) {
        this.sender = sender;
        this.quarter = quarter;
        this.cause = cause;

        this.player = sender instanceof Player senderPlayer ? senderPlayer : null;
        this.resident = this.player != null ? TownyAPI.getInstance().getResident(this.player.getUniqueId()) : null;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return The {@link CommandSender sender} that requested the {@link Quarter quarter} deletion. Null if no person was involved, e.g. unclaiming the {@link com.palmergames.bukkit.towny.object.TownBlock plot}.
     */
    public @Nullable CommandSender getSender() {
        return sender;
    }

    /**
     * @return The {@link Player player} that requested the {@link Quarter quarter} deletion. Null if no person was involved, e.g. unclaiming the {@link com.palmergames.bukkit.towny.object.TownBlock plot}.
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * @return The {@link Resident resident} that requested the {@link Quarter quarter} deletion. Null if no person was involved, e.g. unclaiming the {@link com.palmergames.bukkit.towny.object.TownBlock plot}.
     */
    public @Nullable Resident getResident() {
        return resident;
    }

    /**
     * @return The {@link Quarter quarter} being deleted.
     * @apiNote If you catch this object and keep a reference in cache alive, the garbage collector won't clean it up after the quarter has been deleted.
     */
    public @NotNull Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The {@link QuarterDeleteCause cause} of the quarter's deletion.
     */
    public @NotNull QuarterDeleteCause getCause() {
        return cause;
    }
}
