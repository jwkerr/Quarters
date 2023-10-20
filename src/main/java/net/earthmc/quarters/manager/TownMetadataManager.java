package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterListDataField;

import java.util.List;

public class TownMetadataManager {
    private static final String QLDF = "quarters_qldf";
    private static final String SELL_PRICE = "quarters_sell_price";

    public static List<Quarter> getQuarterListOfTown(Town town) {
        if (town.hasMeta(QLDF)) {
            CustomDataField<?> cdf = town.getMetadata(QLDF);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                List<Quarter> quarterList = qldf.getValue();
                if (quarterList.isEmpty()) {
                    return null;
                } else {
                    return quarterList;
                }
            }
        }

        return null;
    }

    public static void setQuarterListOfTown(Town town, List<Quarter> value) {
        if (!town.hasMeta(QLDF))
            town.addMetaData(new QuarterListDataField("quarters_qldf", null));

        if (town.hasMeta(QLDF)) {
            CustomDataField<?> cdf = town.getMetadata(QLDF);
            if (cdf instanceof QuarterListDataField) {
                QuarterListDataField qldf = (QuarterListDataField) cdf;

                qldf.setValue(value);
                town.addMetaData(qldf);
            }
        }
    }

    public static Double getDefaultSellPriceOfTown(Town town) {
        StringDataField sdf = (StringDataField) town.getMetadata(SELL_PRICE);
        if (sdf == null)
            return null;

        double price;
        try {
            price = Double.parseDouble(sdf.getValue());
        } catch (NumberFormatException e) {
            return null;
        }

        return price;
    }

    public static void setDefaultSellPriceOfTown(Town town, Double price) {
        if (!town.hasMeta(SELL_PRICE))
            town.addMetaData(new StringDataField(SELL_PRICE, null));

        StringDataField sdf = (StringDataField) town.getMetadata(SELL_PRICE);
        if (sdf == null)
            return;

        sdf.setValue(price.toString());
        town.addMetaData(sdf);
    }
}
