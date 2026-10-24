package au.lupine.quarters.object.base;

import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.metadata.*;
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

    public boolean getMetadataAsBoolean(@NotNull T townyObject, @NotNull String key, boolean def) {
        BooleanDataField bdf = (BooleanDataField) townyObject.getMetadata(key);
        if (bdf == null) return def;

        return bdf.getValue();
    }

    public void setMetadataAsInteger(@NotNull T townyObject, @NotNull String key, int value) {
        IntegerDataField idf = new IntegerDataField(key, value);
        townyObject.addMetaData(idf);
    }

    public @Nullable Integer getMetadataAsInteger(@NotNull T townyObject, @NotNull String key) {
        IntegerDataField idf = (IntegerDataField) townyObject.getMetadata(key);
        if (idf == null) return null;

        return idf.getValue();
    }

    public int getMetadataAsInteger(@NotNull T townyObject, @NotNull String key, int def) {
        IntegerDataField idf = (IntegerDataField) townyObject.getMetadata(key);
        if (idf == null) return def;

        return idf.getValue();
    }

    public long getMetadataAsLong(@NotNull T townyObject, @NotNull String key, long def) {
        LongDataField idf = (LongDataField) townyObject.getMetadata(key);
        if (idf == null) return def;

        return idf.getValue();
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

    public Double getMetadataAsDecimal(@NotNull T townyObject, @NotNull String key, Double def) {
        DecimalDataField ddf = (DecimalDataField) townyObject.getMetadata(key);
        if (ddf == null) return def;

        return ddf.getValue();
    }

    public void setMetadataAsString(@NotNull T townyObject, @NotNull String key, String value) {
        StringDataField sdf = new StringDataField(key, value);
        townyObject.addMetaData(sdf);
    }

    public @Nullable String getMetadataAsString(@NotNull T townyObject, @NotNull String key) {
        StringDataField sdf = (StringDataField) townyObject.getMetadata(key);
        if (sdf == null) return null;

        return sdf.getValue();
    }

    public String getMetadataAsString(@NotNull T townyObject, @NotNull String key, String def) {
        StringDataField sdf = (StringDataField) townyObject.getMetadata(key);
        if (sdf == null) return def;

        return sdf.getValue();
    }
}
