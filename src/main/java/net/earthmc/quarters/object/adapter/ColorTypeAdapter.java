package net.earthmc.quarters.object.adapter;

import com.google.gson.*;
import net.earthmc.quarters.api.manager.ConfigManager;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return JsonNull.INSTANCE;

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("red", src.getRed());
        jsonObject.addProperty("green", src.getGreen());
        jsonObject.addProperty("blue", src.getBlue());
        jsonObject.addProperty("alpha", src.getAlpha());

        return jsonObject;
    }

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        JsonObject jsonObject = json.getAsJsonObject();

        try {
            int r = jsonObject.get("red").getAsInt();
            int g = jsonObject.get("green").getAsInt();
            int b = jsonObject.get("blue").getAsInt();
            int a = jsonObject.get("alpha").getAsInt();

            return new Color(r, g, b, a);
        } catch (Exception e) {
            return ConfigManager.getDefaultQuarterColour();
        }
    }
}
