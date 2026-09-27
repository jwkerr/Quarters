package au.lupine.quarters.api.event;

import au.lupine.quarters.object.base.CancellableQuartersEvent;
import au.lupine.quarters.object.entity.Quarter;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.damage.TownyPlayerDamagePlayerEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called before a {@link Player player} takes pvp damage. Cancelling this event prevents the {@link Player player} from taking damage. Useful when you want to modify the behaviour of the {@link au.lupine.quarters.object.state.FlagType PVP flag}.
 * @since 2.0.0
 * @author pernio
 */
public class QuarterPrePvpEvent extends CancellableQuartersEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final TownyPlayerDamagePlayerEvent townyEvent;
    private final @Nullable Quarter quarter;

    /**
     * Creates a {@link Quarter quarter} pre pvp event.
     * @param townyEvent The {@link TownyPlayerDamagePlayerEvent event} that's used to create separate read-only methods.
     * @param quarter The {@link Quarter quarter} where the victim is standing in, null if they were not standing in a quarter
     */
    public QuarterPrePvpEvent(@NotNull TownyPlayerDamagePlayerEvent townyEvent, @Nullable Quarter quarter) {
        this.townyEvent = townyEvent;
        this.quarter = quarter;
    }

    /**
     * @return The {@link Player player} dealing damage.
     */
    public @NotNull Player getAttackingPlayer() {
        return townyEvent.getAttackingPlayer();
    }

    /**
     * @return The {@link Resident resident} dealing damage, null if the {@link Player player} isn't registered in Towny.
     */
    public @Nullable Resident getAttackingResident() {
        return townyEvent.getAttackingResident();
    }

    /**
     * @return The {@link Town town} of the {@link Player player} dealing damage, null if they are not part of a town.
     */
    public @Nullable Town getAttackerTown() {
        return townyEvent.getAttackerTown();
    }

    /**
     * @return The {@link Player player} receiving damage.
     */
    public @NotNull Player getVictimPlayer() {
        return townyEvent.getVictimPlayer();
    }

    /**
     * @return The {@link Resident resident} receiving damage, null if the {@link Player player} isn't registered in Towny.
     */
    public @Nullable Resident getVictimResident() {
        return townyEvent.getVictimResident();
    }

    /**
     * @return The {@link Town town} of the {@link Player player} receiving damage, null if they are not part of a town.
     */
    public @Nullable Town getVictimTown() {
        return townyEvent.getVictimTown();
    }

    /**
     * @return The {@link Quarter quarter} where the victim is standing in, null if they were not standing in a quarter
     */
    public @Nullable Quarter getQuarter() {
        return quarter;
    }

    /**
     * @return The {@link Town town} where the victim is standing in, null if they were not standing in a town
     */
    public @Nullable Town getTown() {
        return TownyAPI.getInstance().getTown(getVictimPlayer().getLocation());
    }

    /**
     * @return The {@link org.bukkit.event.entity.EntityDamageEvent.DamageCause cause} of the damage
     */
    public @NotNull EntityDamageEvent.DamageCause getCause() {
        return townyEvent.getCause();
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        super.setCancelled(isCancelled);
        townyEvent.setCancelled(isCancelled);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
