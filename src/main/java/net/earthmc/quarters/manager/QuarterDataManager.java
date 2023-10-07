package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterListDataField;

import java.util.List;

public class QuarterDataManager {
    private static final String keyName = "quarters_qldf";

    public static List<Quarter> getQuarterListFromTown(Town town) {
        if (town.hasMeta(keyName)) {
            CustomDataField<?> cdf = town.getMetadata(keyName);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                return qldf.getValue();
            }
        }

        return null;
    }

    public static void updateQuarterListOfTown(Town town, List<Quarter> updatedVal) {
        if (!town.hasMeta(keyName))
            town.addMetaData(new QuarterListDataField("quarters_qldf", null, "Quarters"));

        if (town.hasMeta(keyName)) {
            CustomDataField<?> cdf = town.getMetadata(keyName);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                qldf.setValue(updatedVal);
                town.addMetaData(qldf);
            }
        }
    }
}
