package au.lupine.quarters.object.adapter;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;

public class WorldTypeAdapter implements JsonSerializer<World>, JsonDeserializer<World> {

    @Override
    public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getName());
    }

    @Override
    public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String worldName = json.getAsString();
        return Bukkit.getWorld(worldName);
    }
}
