package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.QuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.QuarterDeleteCause;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Called after a quarter is deleted. This cannot be cancelled and only acts as a read-only event. Useful for logging purposes.
 * @since 2.0.0
 * @author pernio, galacticwarrior9
 */
public class QuarterDeleteEvent extends QuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable CommandSender sender;
    private final QuarterDeleteCause cause;
    private final @Nullable UUID owner;
    private final @Nullable Resident ownerResident;
    private final Town town;
    private final @Nullable Nation nation;

    /**
     * Creates a post quarter delete event.
     * @param sender The person who caused/requested to delete the {@link Quarter quarter}. Null if no person was involved, e.g. unclaiming the {@link com.palmergames.bukkit.towny.object.TownBlock plot}.
     * @param cause The cause of why the quarter was deleted.
     * @param owner The {@link UUID} of the player that owned the {@link Quarter quarter}. Null if no one owned it.
     * @param ownerResident The {@link Resident resident} that owned the {@link Quarter quarter}. Null if no one owned it.
     * @param town The town the {@link Quarter quarter} belonged to.
     */
    public QuarterDeleteEvent(@Nullable CommandSender sender, @NotNull QuarterDeleteCause cause, @Nullable UUID owner, @Nullable Resident ownerResident, @NotNull Town town) {
        this.sender = sender;
        this.cause = cause;
        this.owner = owner;
        this.ownerResident = ownerResident;
        this.town = town;
        this.nation = town.getNationOrNull();
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
     * @return The {@link QuarterDeleteCause cause} of the quarter's deletion.
     */
    public @NotNull QuarterDeleteCause getCause() {
        return cause;
    }

    /**
     * @return The {@link UUID} of the player that owned the {@link Quarter quarter}. Null if no one owned it.
     */
    public @Nullable UUID getOwner() {
        return owner;
    }

    /**
     * @return The {@link Resident resident} that owned the {@link Quarter quarter}. Null if no one owned it.
     */
    public @Nullable Resident getOwnerResident() {
        return ownerResident;
    }

    /**
     * @return The {@link Town town} where the quarter belonged to.
     */
    public @NotNull Town getTown() {
        return town;
    }

    /**
     * @return The {@link Nation nation} where the quarter belonged to. Null if it didn't belong to a nation.
     */
    public @Nullable Nation getNation() {
        return nation;
    }
}
