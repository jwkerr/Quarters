package net.earthmc.quarters.object.adapter;

import com.google.gson.*;
import net.earthmc.quarters.object.entity.Cuboid;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class CuboidTypeAdapter implements JsonSerializer<Cuboid>, JsonDeserializer<Cuboid> {

    @Override
    public JsonElement serialize(Cuboid src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return JsonNull.INSTANCE;

        JsonObject jsonObject = new JsonObject();

        jsonObject.add("cornerOne", context.serialize(src.getCornerOne()));
        jsonObject.add("cornerTwo", context.serialize(src.getCornerTwo()));

        return jsonObject;
    }

    @Override
    public Cuboid deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        JsonObject jsonObject = json.getAsJsonObject();

        Location cornerOne = context.deserialize(jsonObject.get("cornerOne").getAsJsonObject(), Location.class);
        Location cornerTwo = context.deserialize(jsonObject.get("cornerTwo").getAsJsonObject(), Location.class);

        return new Cuboid(cornerOne, cornerTwo);
    }
}
