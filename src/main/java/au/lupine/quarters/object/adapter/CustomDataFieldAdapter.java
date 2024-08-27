package au.lupine.quarters.object.adapter;

import au.lupine.quarters.Quarters;
import com.google.gson.*;
import com.palmergames.bukkit.towny.object.metadata.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public class CustomDataFieldAdapter implements JsonSerializer<CustomDataField<?>>, JsonDeserializer<CustomDataField<?>> {

    @Override
    public JsonElement serialize(CustomDataField<?> src, Type typeOfSrc, JsonSerializationContext context) {
        String serialised;
        try {
            serialised = serialiseCDF(src);
        } catch (Exception e) {
            Quarters.logSevere(e.getMessage());
            return JsonNull.INSTANCE;
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("type_id", src.getTypeID());
        jsonObject.addProperty("key", src.getKey());
        jsonObject.addProperty("value", serialised);

        String label = src.getLabel();
        jsonObject.addProperty("label", label.equals("nil") ? null : label);

        return jsonObject;
    }

    @Override
    public CustomDataField<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        Map<String, DataFieldDeserializer<?>> deserialiserMap = getDeserialiserMap();
        if (deserialiserMap == null) return null;

        JsonObject jsonObject = json.getAsJsonObject();

        String typeID = jsonObject.get("type_id").getAsString();
        String key = jsonObject.get("key").getAsString();
        String value = jsonObject.get("value").getAsString();

        JsonElement labelElement = jsonObject.get("label");
        String label = labelElement == null ? null : labelElement.getAsString();

        DataFieldDeserializer<?> deserialiser = deserialiserMap.get(typeID);
        if (deserialiser == null) return null;

        CustomDataField<?> cdf = deserialiser.deserialize(key, value);
        if (cdf == null) return null;

        if (label != null) cdf.setLabel(label);

        return deserialiser.deserialize(key, value);
    }

    private String serialiseCDF(CustomDataField<?> src) throws Exception {
        Class<?> clazz = src.getClass();

        Method method;
        try {
            method = clazz.getDeclaredMethod("serializeValueToString");
        } catch (NoSuchMethodException e) { // CustomDataField implementation does not override method, try superclass
            try {
                clazz = clazz.getSuperclass();
                method = clazz.getDeclaredMethod("serializeValueToString");
            } catch (NoSuchMethodException e2) {
                throw new Exception("Failed to get method serializeValueToString in class " + clazz.getName() + "\n" + e);
            }
        }

        method.setAccessible(true);

        try {
            return (String) method.invoke(src);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new Exception("Failed to invoke method serializeValueToString in class " + clazz.getName() + "\n" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable Map<String, DataFieldDeserializer<?>> getDeserialiserMap() {
        MetadataLoader loader = MetadataLoader.getInstance();

        Class<?> clazz = loader.getClass();

        Field field;
        try {
            field = clazz.getDeclaredField("deserializerMap");
        } catch (NoSuchFieldException e) {
            Quarters.logSevere("Failed to get field deserializerMap in " + clazz.getName());
            return null;
        }

        field.setAccessible(true);

        Map<String, DataFieldDeserializer<?>> deserialiserMap;
        try {
            deserialiserMap = (Map<String, DataFieldDeserializer<?>>) field.get(loader);
        } catch (IllegalAccessException e) {
            Quarters.logSevere("Failed to cast field deserializerMap in " + clazz.getName());
            return null;
        }

        return deserialiserMap;
    }
}
