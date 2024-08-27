package au.lupine.quarters.api.manager;

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
    public static final String HAS_RECEIVED_FREE_WAND_KEY = METADATA_PREFIX + "has_received_free_wand";
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
        return getMetadataAsBoolean(resident, HAS_ENTRY_NOTIFICATIONS_KEY, ConfigManager.getQuarterEntryNotificationsOnByDefault());
    }

    public void setEntryNotificationType(@NotNull Resident resident, @NotNull EntryNotificationType type) {
        setMetadataAsString(resident, ENTRY_NOTIFICATION_TYPE_KEY, type.toString());
    }

    public @NotNull EntryNotificationType getEntryNotificationType(@NotNull Resident resident) {
        EntryNotificationType def = ConfigManager.getDefaultQuarterEntryNotificationType();
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
        return getMetadataAsBoolean(resident, HAS_ENTRY_BLINKING_KEY, ConfigManager.getEntryParticleBlinkingOnByDefault());
    }

    public void setHasConstantOutlines(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY, value);
    }

    public boolean hasConstantOutlines(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY, ConfigManager.getConstantParticleOutlinesOnByDefault());
    }

    public void setHasReceivedFreeWand(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_RECEIVED_FREE_WAND_KEY, value);
    }

    public boolean hasReceivedFreeWand(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_RECEIVED_FREE_WAND_KEY);
    }

    public void setParticleSize(@NotNull Resident resident, float value) {
        setMetadataAsDecimal(resident, PARTICLE_SIZE_KEY, (double) value);
    }

    public float getParticleSize(@NotNull Resident resident) {
        return getMetadataAsDecimal(resident, PARTICLE_SIZE_KEY, (double) ConfigManager.getDefaultParticleSize()).floatValue();
    }
}
