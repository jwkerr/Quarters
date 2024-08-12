package net.earthmc.quarters.object.adapter;

import com.google.gson.*;
import net.earthmc.quarters.object.entity.Cuboid;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class CuboidTypeAdapter implements JsonSerializer<Cuboid>, JsonDeserializer<Cuboid> {

    @Override
    public JsonElement serialize(Cuboid src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        LocationTypeAdapter adapter = new LocationTypeAdapter();
        jsonObject.add("cornerOne", adapter.serialize(src.getCornerOne(), Location.class, context));
        jsonObject.add("cornerTwo", adapter.serialize(src.getCornerTwo(), Location.class, context));

        return jsonObject;
    }

    @Override
    public Cuboid deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        LocationTypeAdapter adapter = new LocationTypeAdapter();

        Location cornerOne = adapter.deserialize(jsonObject.get("cornerOne").getAsJsonObject(), Location.class, context);
        Location cornerTwo = adapter.deserialize(jsonObject.get("cornerTwo").getAsJsonObject(), Location.class, context);

        return new Cuboid(cornerOne, cornerTwo);
    }
}
