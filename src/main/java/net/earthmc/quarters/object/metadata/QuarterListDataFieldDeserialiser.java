package net.earthmc.quarters.object.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.palmergames.bukkit.towny.object.metadata.DataFieldDeserializer;
import net.earthmc.quarters.api.manager.JSONManager;
import net.earthmc.quarters.object.entity.Quarter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class QuarterListDataFieldDeserialiser implements DataFieldDeserializer<QuarterListDataField> {

    @Override
    public @Nullable QuarterListDataField deserialize(@NotNull String key, @Nullable String value) {
        if (value == null) return null;

        List<Quarter> quarters = new ArrayList<>();

        byte[] bytes = Base64.getDecoder().decode(value);
        String decoded = new String(bytes);

        Gson gson = JSONManager.getInstance().getGson();
        JsonArray jsonArray = gson.fromJson(decoded, JsonArray.class);
        for (JsonElement element : jsonArray) {
            Quarter quarter = gson.fromJson(element.toString(), Quarter.class);
            quarters.add(quarter);
        }

        return new QuarterListDataField(key, quarters);
    }
}
