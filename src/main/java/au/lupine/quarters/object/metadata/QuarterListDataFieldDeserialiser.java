package au.lupine.quarters.object.metadata;

import au.lupine.quarters.api.manager.JSONManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.FlagType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.palmergames.bukkit.towny.object.metadata.DataFieldDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuarterListDataFieldDeserialiser implements DataFieldDeserializer<QuarterListDataField> {

    @Override
    public @Nullable QuarterListDataField deserialize(@NotNull String key, @Nullable String value) {
        if (value == null) return new QuarterListDataField(key, new CopyOnWriteArrayList<>());

        List<Quarter> quarters = new CopyOnWriteArrayList<>();

        byte[] bytes = Base64.getDecoder().decode(value);
        String decoded = new String(bytes);

        Gson gson = JSONManager.getInstance().getGson();
        JsonArray jsonArray = gson.fromJson(decoded, JsonArray.class);
        for (JsonElement element : jsonArray) {
            Quarter quarter = gson.fromJson(element, Quarter.class);
            migrateLegacyType(element, quarter);
            quarters.add(quarter);
        }

        return new QuarterListDataField(key, quarters);
    }

    private void migrateLegacyType(@NotNull JsonElement element, @NotNull Quarter quarter) {
        if (!element.isJsonObject()) return;

        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement typeElement = jsonObject.get("type");
        if (typeElement == null || !typeElement.isJsonPrimitive()) return;

        switch (typeElement.getAsString().toUpperCase()) {
            case "INN" -> quarter.setFlag(FlagType.SLEEPING, true);
            case "STATION" -> quarter.setFlag(FlagType.VEHICLES, true);
        }
    }
}
