package net.earthmc.quarters.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.earthmc.quarters.Quarters;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;

public class SponsorCosmeticsManager {
    public static HashMap<UUID, String> sponsorMap = new HashMap<>();

    public static void init() {
        InputStream inputStream = Quarters.INSTANCE.getResource("sponsors.json");
        if (inputStream == null)
            return;

        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        populateHashMap(jsonObject.getAsJsonArray("authors"), "authors");
        populateHashMap(jsonObject.getAsJsonArray("early_supporters"), "early_supporters");
    }

    private static void populateHashMap(JsonArray jsonArray, String type) {
        for (int i = 0; i< jsonArray.size(); i++) {
            UUID uuid = UUID.fromString(jsonArray.get(i).getAsString());
            sponsorMap.put(uuid, type);
        }
    }
}
