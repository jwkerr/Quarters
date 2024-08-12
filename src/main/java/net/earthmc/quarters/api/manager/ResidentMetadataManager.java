package net.earthmc.quarters.api.manager;

import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.quarters.object.base.MetadataManager;
import org.jetbrains.annotations.NotNull;

public final class ResidentMetadataManager extends MetadataManager<Resident> {

    private static ResidentMetadataManager instance;

    public static final String HAS_CONSTANT_OUTLINES_KEY = METADATA_PREFIX + "has_constant_outlines";

    private ResidentMetadataManager() {}

    public static ResidentMetadataManager getInstance() {
        if (instance == null) instance = new ResidentMetadataManager();
        return instance;
    }

    public void setHasConstantOutlines(@NotNull Resident resident, boolean value) {
        setMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY, value);
    }

    public boolean hasConstantOutlines(@NotNull Resident resident) {
        return getMetadataAsBoolean(resident, HAS_CONSTANT_OUTLINES_KEY);
    }
}
