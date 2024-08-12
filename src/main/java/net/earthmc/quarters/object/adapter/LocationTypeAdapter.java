package net.earthmc.quarters.object.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.serialize());
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Type serialisedType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = context.deserialize(json, serialisedType);

        return Location.deserialize(map);
    }
}
