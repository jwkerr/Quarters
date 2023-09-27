package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterListDataField;

import java.util.List;

public class QuarterDataManager {
    private static final String keyName = "quarters_qldf";

    public static List<Quarter> getQuarterListFromTownBlock(TownBlock townBlock) {
        if (townBlock.hasMeta(keyName)) {
            CustomDataField<?> cdf = townBlock.getMetadata(keyName);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                return qldf.getValue();
            }
        }

        return null;
    }

    public static void updateQuarterListOfTownBlock(TownBlock townBlock, List<Quarter> updatedVal) {
        if (!townBlock.hasMeta(keyName))
            townBlock.addMetaData(new QuarterListDataField("quarters_qldf", null, "Quarter List Data Field"));

        if (townBlock.hasMeta(keyName)) {
            CustomDataField<?> cdf = townBlock.getMetadata(keyName);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                qldf.setValue(updatedVal);
            }
        }
    }
}
