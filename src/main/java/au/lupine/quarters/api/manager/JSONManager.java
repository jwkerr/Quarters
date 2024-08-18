package au.lupine.quarters.api.manager;

import au.lupine.quarters.object.adapter.*;
import au.lupine.quarters.object.entity.Cuboid;
import au.lupine.quarters.object.wrapper.QuarterPermissions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.World;

import java.awt.*;

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
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeAdapter(Cuboid.class, new CuboidTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(QuarterPermissions.class, new QuarterPermissionsTypeAdapter())
                .registerTypeAdapter(Town.class, new TownTypeAdapter())
                .registerTypeAdapter(World.class, new WorldTypeAdapter())
                .create();
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
}