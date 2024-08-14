package net.earthmc.quarters.object.metadata;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.api.manager.JSONManager;
import net.earthmc.quarters.api.manager.TownMetadataManager;
import net.earthmc.quarters.object.entity.Quarter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class QuarterListDataField extends CustomDataField<List<Quarter>> {

    public QuarterListDataField(String key, List<Quarter> value, String label) {
        super(key, value, label);
    }

    public QuarterListDataField(String key, List<Quarter> value) {
        super(key, value);
    }

    public static @NotNull String typeID() {
        return TownMetadataManager.QUARTER_LIST_KEY;
    }

    @Override
    public @NotNull String getTypeID() {
        return typeID();
    }

    @Override
    public void setValueFromString(String string) {
        Type quarterListType = new TypeToken<List<Quarter>>(){}.getType();

        try {
            this.setValue(JSONManager.getInstance().getGson().fromJson(string, quarterListType));
        } catch (JsonSyntaxException e) {
            Quarters.logSevere("Failed to set value of quarter list from string" + string);
            this.setValue(null);
        }
    }

    @Override
    protected String displayFormattedValue() {
       return getValue().toString();
    }

    @Override
    protected @Nullable String serializeValueToString() {
        Gson gson = JSONManager.getInstance().getGson();
        String json = gson.toJson(getValue());

        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    @Override
    public @NotNull CustomDataField<List<Quarter>> clone() {
        return new QuarterListDataField(getKey(), new ArrayList<>(getValue()), hasLabel() ? getLabel() : null);
    }
}
