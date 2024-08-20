package au.lupine.quarters.object.metadata;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.JSONManager;
import au.lupine.quarters.api.manager.TownMetadataManager;
import au.lupine.quarters.object.entity.Quarter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Quarters uses Towny metadata to store all of its data, as it is only usable with Towny this significantly simplifies things in terms of development
 * <p>
 * To put it simply, this class is used by Towny to run the {@link #serializeValueToString()} method which encodes a town's quarters as a Base64 encoded JSON object to prevent formatting issues with Towny's own internal storage.
 * Then the {@link QuarterListDataFieldDeserialiser} reverses this serialisation using {@link JSONManager#getGson()} and creates a list of quarters.
 * If you add anything that cannot be trivially serialised by GSON to the {@link Quarter} class, you must write a custom type adapter for it.
 * @see <a href="https://github.com/TownyAdvanced/Towny/wiki/Configuring-Metadata-in-Towny-objects.">Configuring Metadata in Towny objects</a>
 * @see <a href="https://github.com/TownyAdvanced/Towny/wiki/Creating-Custom-Metadata-Types">Creating Custom Metadata Types</a>
 * @see au.lupine.quarters.object.adapter
 */
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
            setValue(JSONManager.getInstance().getGson().fromJson(string, quarterListType));
        } catch (JsonSyntaxException e) {
            Quarters.logSevere("Failed to set value of quarter list from string" + string);
            setValue(new ArrayList<>());
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
