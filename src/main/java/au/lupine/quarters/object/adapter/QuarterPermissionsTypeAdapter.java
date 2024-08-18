package au.lupine.quarters.object.adapter;

import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import au.lupine.quarters.object.wrapper.QuarterPermissions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class QuarterPermissionsTypeAdapter implements JsonSerializer<QuarterPermissions>, JsonDeserializer<QuarterPermissions> {

    @Override
    public JsonElement serialize(QuarterPermissions src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (ActionType type : ActionType.values()) {
            Map<PermLevel, Boolean> perms = src.getPermissions(type);
            jsonObject.add(type.name(), context.serialize(perms));
        }

        return jsonObject;
    }

    @Override
    public QuarterPermissions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Type serialisedType = new TypeToken<Map<PermLevel, Boolean>>(){}.getType();

        QuarterPermissions permissions = new QuarterPermissions();
        for (ActionType type : ActionType.values()) {
            Map<PermLevel, Boolean> perms = context.deserialize(jsonObject.get(type.name()), serialisedType);
            permissions.setPermissions(type, perms);
        }

        return permissions;
    }
}
