package net.earthmc.quarters.api.manager;

import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.base.MetadataManager;
import org.jetbrains.annotations.NotNull;

public final class ResidentMetadataManager extends MetadataManager<Resident> {

    private static ResidentMetadataManager instance;

    public static final String HAS_ENTRY_NOTIFICATIONS_KEY = METADATA_PREFIX + "has_entry_notifications";
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
        Double value = getMetadataAsDecimal(resident, PARTICLE_SIZE_KEY);
        return value == null ? ConfigManager.getDefaultParticleSize() : value.floatValue();
    }
}
