package au.lupine.quarters.object.adapter;

import au.lupine.quarters.Quarters;
import com.google.gson.*;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.object.metadata.DataFieldIO;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class CustomDataFieldAdapter implements JsonSerializer<CustomDataField<?>>, JsonDeserializer<CustomDataField<?>> {

    @Override
    public JsonElement serialize(CustomDataField<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray jsonArray = serialiseCDF(src);
        if (jsonArray == null) return JsonNull.INSTANCE;

        return jsonArray;
    }

    private JsonArray serialiseCDF(CustomDataField<?> src) {
        DataFieldIO dfio = new DataFieldIO();

        Method method;
        try {
            method = dfio.getClass().getDeclaredMethod("serializeCDF", CustomDataField.class);
        } catch (NoSuchMethodException e) {
            Quarters.logSevere("Failed to get method serializeCDF in class DataFieldIO");
            return null;
        }

        method.setAccessible(true);

        try {
            return (JsonArray) method.invoke(null, src);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Quarters.logSevere("Failed to invoke method serializeCDF in class DataFieldIO");
            return null;
        }
    }

    @Override
    public CustomDataField<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) return null;

        Object rdf = deserialiseCDFToRaw(json.getAsJsonArray());
        if (rdf == null) return null;

        return convertRawMetadata(rdf);
    }

    private @Nullable Object deserialiseCDFToRaw(JsonArray jsonArray) {
        DataFieldIO dfio = new DataFieldIO();

        Method method;
        try {
            method = dfio.getClass().getDeclaredMethod("deserializeCDFToRaw", JsonArray.class);
        } catch (NoSuchMethodException e) {
            Quarters.logSevere("Failed to get method deserializeCDFToRaw in class DataFieldIO");
            return null;
        }

        method.setAccessible(true);

        try {
            return method.invoke(null, jsonArray);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            Quarters.logSevere("Failed to invoke method deserializeCDFToRaw in class DataFieldIO");
            return null;
        }
    }

    private @Nullable CustomDataField<?> convertRawMetadata(Object rdf) {
        MetadataLoader loader = MetadataLoader.getInstance();

        Method method;
        try {
            method = loader.getClass().getDeclaredMethod("convertRawMetadata", Class.forName("com.palmergames.bukkit.towny.object.metadata.RawDataField"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            Quarters.logSevere("Failed to get method convertRawMetadata in class MetadataLoader");
            return null;
        }

        method.setAccessible(true);

        try {
            return (CustomDataField<?>) method.invoke(loader, rdf);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            Quarters.logSevere("Failed to invoke method convertRawMetadata in class MetadataLoader");
            return null;
        }
    }
}
