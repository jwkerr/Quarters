package net.earthmc.quarters.object.adapter;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return JsonNull.INSTANCE;
        return context.serialize(src.serialize());
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        Type serialisedType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = context.deserialize(json, serialisedType);

        return Location.deserialize(map);
    }
}
