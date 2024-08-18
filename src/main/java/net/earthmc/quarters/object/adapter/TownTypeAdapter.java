package net.earthmc.quarters.object.adapter;

import com.google.gson.*;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

import java.lang.reflect.Type;
import java.util.UUID;

public class TownTypeAdapter implements JsonSerializer<Town>, JsonDeserializer<Town> {

    @Override
    public JsonElement serialize(Town src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUUID().toString());
    }

    @Override
    public Town deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        UUID uuid = UUID.fromString(json.getAsString());
        return TownyAPI.getInstance().getTown(uuid);
    }
}
