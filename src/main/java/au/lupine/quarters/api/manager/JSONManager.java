package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.event.PreBuildGsonEvent;
import au.lupine.quarters.object.adapter.*;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.wrapper.QuarterPermissions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public final class JSONManager {

    private static JSONManager instance;
    private boolean isSetup = false;

    private Gson gson;

    private JSONManager() {}

    public static JSONManager getInstance() {
        if (instance == null) instance = new JSONManager();
        return instance;
    }

    private void setup() {
        GsonBuilder builder = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeAdapter(Cuboid.class, new CuboidTypeAdapter())
                .registerTypeAdapter(CustomDataField.class, new CustomDataFieldAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(QuarterPermissions.class, new QuarterPermissionsTypeAdapter())
                .registerTypeAdapter(Town.class, new TownTypeAdapter())
                .registerTypeAdapter(World.class, new WorldTypeAdapter());

        PreBuildGsonEvent event = new PreBuildGsonEvent(builder);
        event.callEvent();

        gson = builder.create();
    }

    /**
     * @return A Gson instance with type adapters and config settings for Quarters already registered
     */
    public Gson getGson() {
        if (!isSetup) {
            setup();
            isSetup = true;
        }

        return gson;
    }

    public <T extends JsonElement> @Nullable T getUrlAsJsonElement(String urlString, Class<T> type) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code != 200) return null;

            StringBuilder text = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                text.append(scanner.nextLine());
            }

            scanner.close();

            return JSONManager.getInstance().getGson().fromJson(text.toString(), type);
        } catch (Exception e) {
            Quarters.logWarning(String.valueOf(e));
            return null;
        }
    }
}
