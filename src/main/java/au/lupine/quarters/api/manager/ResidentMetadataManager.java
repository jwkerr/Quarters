package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.base.MetadataManager;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.object.Resident;
import org.jetbrains.annotations.NotNull;

public final class ResidentMetadataManager extends MetadataManager<Resident> {

    private static ResidentMetadataManager instance;

    public static final String HAS_ENTRY_NOTIFICATIONS_KEY = METADATA_PREFIX + "has_entry_notifications";
    public static final String ENTRY_NOTIFICATION_TYPE_KEY = METADATA_PREFIX + "entry_notification_type";
    public static final String HAS_ENTRY_BLINKING_KEY = METADATA_PREFIX + "has_entry_blinking";
    public static final String HAS_CONSTANT_OUTLINES_KEY = METADATA_PREFIX + "has_constant_outlines";
    public static final String AMOUNT_RECEIVED_FREE_WAND_KEY = METADATA_PREFIX + "free_wands_received";
    public static final String LAST_RECEIVED_FREE_WAND_KEY = METADATA_PREFIX + "last_received_free_wand";
    public static final String PARTICLE_SIZE_KEY = METADATA_PREFIX + "particle_size";

    private ResidentMetadataManager() {}

    public static ResidentMetadataManager getInstance() {
        if (instance == null) instance = new ResidentMetadataManager();
        return instance;
    }

    public void setHasEntryNotifications(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_ENTRY_NOTIFICATIONS_KEY, value);
    }

    public boolean hasEntryNotifications(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_ENTRY_NOTIFICATIONS_KEY, Quarters.getInstance().config().quarters.quarterEntryNotificationsOnByDefault);
    }

    public void setEntryNotificationType(@NotNull Resident resident, @NotNull EntryNotificationType type) {
        setMetadataAsString(resident, ENTRY_NOTIFICATION_TYPE_KEY, type.toString());
    }

    public @NotNull EntryNotificationType getEntryNotificationType(@NotNull Resident resident) {
        EntryNotificationType def = Quarters.getInstance().config().quarters.defaultQuarterEntryNotificationType;
        String typeString = getMetadataAsString(resident, ENTRY_NOTIFICATION_TYPE_KEY, def.toString());

        try {
            return EntryNotificationType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public void setHasEntryBlinking(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_ENTRY_BLINKING_KEY, value);
    }

    public boolean hasEntryBlinking(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_ENTRY_BLINKING_KEY, Quarters.getInstance().config().particles.entryParticleBlinkingOnByDefault);
    }

    public void setHasConstantOutlines(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY, value);
    }

    public boolean hasConstantOutlines(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY, Quarters.getInstance().config().particles.constantParticleOutlinesOnByDefault);
    }

    public void incrementReceivedFreeWands(@NotNull Resident resident) {
        int freeWandsReceived = getMetadataAsInteger(resident, AMOUNT_RECEIVED_FREE_WAND_KEY, 0);
        // Protect against integer overflows
        if (freeWandsReceived == Integer.MAX_VALUE) return;
        setMetadataAsInteger(resident, AMOUNT_RECEIVED_FREE_WAND_KEY, freeWandsReceived + 1);
    }

    public boolean canReceiveFreeWand(@NotNull Resident resident) {
        int maxFreeWands = Math.clamp(Quarters.getInstance().config().wand.freeAmount, -1, Integer.MAX_VALUE);
        // Always allow a free wand if -1
        if (maxFreeWands == -1) return true;
        return getMetadataAsInteger(resident, AMOUNT_RECEIVED_FREE_WAND_KEY, 0) < maxFreeWands;
    }

    public void setLastReceivedFreeWand(@NotNull Resident resident) {
        setMetadataAsString(resident, LAST_RECEIVED_FREE_WAND_KEY, String.valueOf(System.currentTimeMillis()));
    }

    public long getLastReceivedFreeWand(@NotNull Resident resident) {
        return getMetadataAsLong(resident, LAST_RECEIVED_FREE_WAND_KEY, 0L);
    }

    public long getRemainingFreeWandCooldown(@NotNull Resident resident) {
        long cooldownSeconds = Math.max(Quarters.getInstance().config().wand.cooldownSeconds, 0);

        long lastReceived = getLastReceivedFreeWand(resident);
        long cooldownMillis = cooldownSeconds * 1000L;
        long remainingMillis = (lastReceived + cooldownMillis) - System.currentTimeMillis();
        if (remainingMillis <= 0) return 0;

        // Round up, so 1 ms remaining still displays as 1 second.
        return (remainingMillis + 999) / 1000;
    }

    public void setParticleSize(@NotNull Resident resident, float value) {
        setMetadataAsDecimal(resident, PARTICLE_SIZE_KEY, (double) value);
    }

    public float getParticleSize(@NotNull Resident resident) {
        return getMetadataAsDecimal(resident, PARTICLE_SIZE_KEY, (double) Quarters.getInstance().config().particles.defaultParticleSize).floatValue();
    }
}
