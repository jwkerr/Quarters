package net.earthmc.quarters.object.base;

import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.DecimalDataField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MetadataManager<T extends TownyObject> {

    public static final String METADATA_PREFIX = "Quarters_";

    public void setMetadataAsBoolean(@NotNull T townyObject, @NotNull String key, Boolean value) {
        BooleanDataField bdf = new BooleanDataField(key, value);
        townyObject.addMetaData(bdf);
    }

    public boolean getMetadataAsBoolean(@NotNull T townyObject, @NotNull String key) {
        BooleanDataField bdf = (BooleanDataField) townyObject.getMetadata(key);
        if (bdf == null) return false;

        return bdf.getValue();
    }

    public void setMetadataAsDecimal(@NotNull T townyObject, @NotNull String key, Double value) {
        DecimalDataField ddf = new DecimalDataField(key, value);
        townyObject.addMetaData(ddf);
    }

    public @Nullable Double getMetadataAsDecimal(@NotNull T townyObject, @NotNull String key) {
        DecimalDataField ddf = (DecimalDataField) townyObject.getMetadata(key);
        if (ddf == null) return null;

        return ddf.getValue();
    }
}
