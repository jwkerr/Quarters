package net.earthmc.quarters.manager;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.earthmc.quarters.object.Quarter;
import net.earthmc.quarters.object.QuarterListDataField;

import java.util.List;

public class TownMetadataManager {
    private static final String QLDF = "quarters_qldf";
    private static final String SELL_PRICE = "quarters_sell_price";
    private static final String SELL_ON_DELETE = "quarters_sell_on_delete";

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

    public static boolean shouldSellOnDelete(Town town) {
        BooleanDataField bdf = (BooleanDataField) town.getMetadata(SELL_ON_DELETE);
        if (bdf == null)
            return false;

        return bdf.getValue();
    }

    public static void setSellOnDelete(Town town, boolean value) {
        if (!town.hasMeta(SELL_ON_DELETE))
            town.addMetaData(new BooleanDataField(SELL_ON_DELETE, null));

        BooleanDataField bdf = (BooleanDataField) town.getMetadata(SELL_ON_DELETE);
        if (bdf == null)
            return;

        bdf.setValue(value);
        town.addMetaData(bdf);
    }
}
