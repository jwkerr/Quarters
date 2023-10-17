package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.bukkit.Material;

public class ResidentMetadataManager {
    private static final String CONSTANT_OUTLINES = "quarters_constant_outlines";
    private static final String WAND = "quarters_custom_wand";

    public static Boolean hasConstantOutlines(Resident resident) {
        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(CONSTANT_OUTLINES);
        if (bdf == null)
            return null;

        return bdf.getValue();
    }

    public static void setConstantOutlines(Resident resident, boolean value) {
        if (!resident.hasMeta(CONSTANT_OUTLINES))
            resident.addMetaData(new BooleanDataField(CONSTANT_OUTLINES, null));

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(CONSTANT_OUTLINES);
        if (bdf == null)
            return;

        bdf.setValue(value);
        resident.addMetaData(bdf);
    }
}
