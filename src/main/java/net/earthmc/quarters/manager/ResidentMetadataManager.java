package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;

public class ResidentMetadataManager {
    private static final String CONSTANT_OUTLINES = "quarters_constant_outlines";

    public static Boolean hasConstantOutlines(Resident resident) {
        if (resident == null)
            return null;

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
