package au.lupine.quarters.api.manager;

import au.lupine.quarters.object.base.MetadataManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.metadata.QuarterListDataField;
import com.palmergames.bukkit.towny.object.Town;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class TownMetadataManager extends MetadataManager<Town> {

    private static TownMetadataManager instance;

    public static final String QUARTER_LIST_KEY = METADATA_PREFIX + "quarter_list";
    public static final String DEFAULT_SELL_PRICE_KEY = METADATA_PREFIX + "default_sell_price";
    public static final String SELL_ON_DELETE_KEY = METADATA_PREFIX + "sell_on_delete";

    private TownMetadataManager() {}

    public static TownMetadataManager getInstance() {
        if (instance == null) instance = new TownMetadataManager();
        return instance;
    }

    public void setQuarterList(@NotNull Town town, List<Quarter> value) {
        setMetadataAsQuarterList(town, QUARTER_LIST_KEY, value);
    }

    public List<Quarter> getQuarterList(@NotNull Town town) {
        return getMetadataAsQuarterList(town, QUARTER_LIST_KEY);
    }

    public void setDefaultSellPrice(@NotNull Town town, Double value) {
        setMetadataAsDecimal(town, DEFAULT_SELL_PRICE_KEY, value);
    }

    public Double getDefaultSellPrice(@NotNull Town town) {
        Double value = getMetadataAsDecimal(town, DEFAULT_SELL_PRICE_KEY);
        return value == null ? 0 : value;
    }

    public void setSellOnDelete(@NotNull Town town, boolean value) {
        setMetadataAsBoolean(town, SELL_ON_DELETE_KEY, value);
    }

    public Boolean getSellOnDelete(@NotNull Town town) {
        return getMetadataAsBoolean(town, SELL_ON_DELETE_KEY);
    }

    private void setMetadataAsQuarterList(@NotNull Town town, @NotNull String key, List<Quarter> value) {
        QuarterListDataField qldf = new QuarterListDataField(key, value);
        town.addMetaData(qldf);
    }

    private List<Quarter> getMetadataAsQuarterList(@NotNull Town town, @NotNull String key) {
        QuarterListDataField qldf = (QuarterListDataField) town.getMetadata(key);
        if (qldf == null) return new ArrayList<>();

        return qldf.getValue();
    }
}
