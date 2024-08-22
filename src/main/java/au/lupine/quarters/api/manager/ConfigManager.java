package au.lupine.quarters.api.manager;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.wrapper.UserGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ConfigManager {

    private static ConfigManager instance;

    private static final String USER_GROUPS_URL = "https://raw.githubusercontent.com/jwkerr/Quarters/master/src/main/resources/user_groups.json";

    private static FileConfiguration config;
    public static final UserGroup DEFAULT_USER_GROUP = new UserGroup();
    private static final List<UserGroup> USER_GROUPS = new ArrayList<>();

    private ConfigManager() {}

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public void setup() {
        load();

        loadUserGroups();
    }

    public void load() {
        Quarters plugin = Quarters.getInstance();
        config = plugin.getConfig();

        addValues();

        plugin.saveConfig();
    }

    public void reload() {
        Quarters plugin = Quarters.getInstance();
        plugin.reloadConfig();

        config = plugin.getConfig();

        loadUserGroups();
    }

    private void loadUserGroups() {
        if (ConfigManager.canPluginRequestUserGroups()) {
            Quarters.logInfo("Requesting user_groups.json from " + USER_GROUPS_URL + " thank you for keeping this setting enabled!");

            loadUserGroupsFromWeb().thenAccept(jsonArray -> {
                if (jsonArray == null) {
                    Quarters.logWarning("An error occurred while requesting user_groups.json, defaulting to jar resources");
                    jsonArray = loadUserGroupsFromResources();
                }

                parseUserGroups(jsonArray);
            });
        } else {
            parseUserGroups(loadUserGroupsFromResources());
        }
    }

    private void parseUserGroups(JsonArray jsonArray) {
        if (jsonArray == null) return; // This is probably only possible if the end-user fucks with jar contents

        USER_GROUPS.clear();

        for (JsonElement element : jsonArray) {
            USER_GROUPS.add(new UserGroup(element.getAsJsonObject()));
        }

        Collections.shuffle(USER_GROUPS);
    }

    private CompletableFuture<@Nullable JsonArray> loadUserGroupsFromWeb() {
        return CompletableFuture.supplyAsync(() -> JSONManager.getInstance().getUrlAsJsonElement(USER_GROUPS_URL, JsonArray.class));
    }

    private @Nullable JsonArray loadUserGroupsFromResources() {
        InputStream inputStream = Quarters.getInstance().getResource("user_groups.json");
        if (inputStream == null) return null; // This shouldn't happen

        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(reader, JsonArray.class);
    }

    public static UserGroup getUserGroupOrDefault(UUID uuid, UserGroup def) {
        for (UserGroup userGroup : USER_GROUPS) {
            if (userGroup.getMembers().contains(uuid)) return userGroup;
        }

        return def;
    }

    public static List<UserGroup> getUserGroups() {
        return USER_GROUPS;
    }

    /**
     * @param uuid UUID of the player you would like to get a formatted name of
     * @param def A default if the UUID doesn't resolve to a player, can be null if you know for a fact the player exists
     * @return A formatted name that can be clicked for /res and has a colour and hover if applicable
     */
    public static Component getFormattedName(@Nullable UUID uuid, @Nullable Component def) {
        if (uuid == null) return def;

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        String name = player.getName();
        if (name == null) return def; // UUID didn't resolve to a player that has joined

        UserGroup userGroup = getUserGroupOrDefault(uuid, DEFAULT_USER_GROUP);

        return userGroup.formatString(name)
                .clickEvent(ClickEvent.runCommand("/towny:resident " + name));
    }

    public static boolean canPluginRequestUserGroups() {
        return config.getBoolean("technical.can_plugin_request_user_groups", true);
    }

    public static Material getWandMaterial() {
        return Material.valueOf(config.getString("wand_material", "FLINT"));
    }

    public static boolean doMayorsBypassCertainElevatedPerms() {
        return config.getBoolean("mayor_bypasses_certain_elevated_perms", true);
    }

    public static int getMaxQuarterVolume() {
        return config.getInt("quarters.max_quarter_volume", -1);
    }

    public static int getMaxQuartersPerTown() {
        return config.getInt("quarters.max_quarters_per_town", -1);
    }

    public static int getMaxCuboidVolume() {
        return config.getInt("quarters.max_cuboid_volume", -1);
    }

    public static int getMaxCuboidsPerQuarter() {
        return config.getInt("quarters.max_cuboids_per_quarter", -1);
    }

    public static boolean hasDefaultQuarterColour() {
        return config.getBoolean("quarters.default_quarter_colour.enabled", false);
    }

    public static Color getDefaultQuarterColour() {
        int r = config.getInt("quarters.default_quarter_colour.red", 63);
        int g = config.getInt("quarters.default_quarter_colour.green", 180);
        int b = config.getInt("quarters.default_quarter_colour.blue", 255);

        return new Color(r, g, b);
    }

    public static boolean areEntryNotificationsAllowed() {
        return config.getBoolean("quarters.allow_entry_notifications", true);
    }

    public static boolean getQuarterEntryNotificationsOnByDefault() {
        return config.getBoolean("quarters.quarter_entry_notifications_on_by_default", true);
    }

    public static boolean areParticlesEnabled() {
        return config.getBoolean("particles.enabled", true);
    }

    public static Particle getCurrentSelectionParticle() {
        return Particle.valueOf(config.getString("particles.current_selection_particle", "SCRAPE"));
    }

    public static Particle getCurrentCuboidsParticle() {
        return Particle.valueOf(config.getString("particles.current_cuboids_particle", "WAX_OFF"));
    }

    public static int getTicksBetweenParticleOutlines() {
        return config.getInt("particles.ticks_between_particle_outlines", 5);
    }

    public static int getMaxDistanceForCuboidParticles() {
        return config.getInt("particles.max_distance_for_cuboid_particles", 48);
    }

    public static float getDefaultParticleSize() {
        return (float) config.getDouble("particles.default_particle_size", 1F);
    }

    public static boolean areConstantParticleOutlinesAllowed() {
        if (!areParticlesEnabled()) return false;
        return config.getBoolean("particles.allow_constant_particle_outlines", true);
    }

    public static boolean getConstantParticleOutlinesOnByDefault() {
        return config.getBoolean("particles.constant_particle_outlines_on_by_default", true);
    }

    public static boolean isEntryParticleBlinkingAllowed() {
        if (!areParticlesEnabled()) return false;
        return config.getBoolean("particles.allow_entry_particle_blinking", true);
    }

    public static boolean getEntryParticleBlinkingOnByDefault() {
        return config.getBoolean("particles.entry_particle_blinking_on_by_default", false);
    }

    private void addValues() {
        config.options().setHeader(List.of("If comments are not present, please restart your server"));

        config.addDefault("technical.can_plugin_request_user_groups", true); config.setInlineComments("technical.can_plugin_request_user_groups", List.of("If set to true, the plugin will be allowed to query GitHub for the latest sponsor data to correctly format names (please keep this enabled as sponsors are what keep development coming!)"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", List.of("Material of the wand item"));
        config.addDefault("mayor_bypasses_certain_elevated_perms", true); config.setInlineComments("mayor_bypasses_certain_elevated_perms", List.of("If this is set to true, mayors will bypass perms for certain command such as /q create, /q evict etc. This is intended to make configuration easier as most servers will want this behaviour"));

        config.addDefault("quarters.max_quarter_volume", -1); config.setInlineComments("quarters.max_quarter_volume", List.of("Maximum block volume of all cuboids in a quarter combined, set to -1 for no limit"));
        config.addDefault("quarters.max_quarters_per_town", -1); config.setInlineComments("quarters.max_quarters_per_town", List.of("Maximum amount of quarters that can be in a single town, set to -1 for no limit"));
        config.addDefault("quarters.max_cuboid_volume", -1); config.setInlineComments("quarters.max_cuboid_volume", List.of("Maximum block volume of individual cuboids, set to -1 for no limit"));
        config.addDefault("quarters.max_cuboids_per_quarter", -1); config.setInlineComments("quarters.max_cuboids_per_quarter", List.of("Maximum amount of cuboids that can be in each quarter, set to -1 for no limit"));
        config.addDefault("quarters.default_quarter_colour.enabled", false); config.setInlineComments("quarters.default_colour.enabled", List.of("Enable to make quarters a certain colour by default, configure colour below"));
        config.addDefault("quarters.default_quarter_colour.red", 63);
        config.addDefault("quarters.default_quarter_colour.green", 180);
        config.addDefault("quarters.default_quarter_colour.blue", 255);
        config.addDefault("quarters.allow_quarter_entry_notifications", true); config.setInlineComments("quarters.allow_quarter_entry_notifications", List.of("If set to true, players will be allowed to toggle notifications of when they have entered a quarter"));
        config.addDefault("quarters.quarter_entry_notifications_on_by_default", true); config.setInlineComments("quarters.quarter_entry_notifications_on_by_default", List.of("If set to false players will have to opt in to entry notifications"));

        config.addDefault("particles.enabled", true); config.setInlineComments("particles.enabled", List.of("Set to false to completely disable particle outlines around cuboids"));
        config.addDefault("particles.current_selection_particle", "SCRAPE"); config.setInlineComments("particles.current_selection_particle", List.of("Particle outline of the currently selected area"));
        config.addDefault("particles.current_cuboids_particle", "WAX_OFF"); config.setInlineComments("particles.current_cuboids_particle", List.of("Particle outline of current cuboids added to selection"));
        config.addDefault("particles.ticks_between_particle_outlines", 5); config.setInlineComments("particles.ticks_between_particle_outlines", List.of("The number of ticks between when the particle outlines of quarters will appear"));
        config.addDefault("particles.max_distance_for_cuboid_particles", 48); config.setInlineComments("particles.max_distance_for_cuboid_particles", List.of("The maximum distance a player can be from a cuboid before the outline particles stop being sent to their client"));
        config.addDefault("particles.default_particle_size", 1F); config.setInlineComments("particles.default_particle_size", List.of("Sets the default size for particles of quarters that have been made"));
        config.addDefault("particles.allow_constant_particle_outlines", true); config.setInlineComments("particles.allow_constant_particle_outlines", List.of("If set to true, players will be able to toggle quarter outlines to display constantly"));
        config.addDefault("particles.constant_particle_outlines_on_by_default", true); config.setInlineComments("particles.constant_particle_outlines_on_by_default", List.of("If set to false players will have to opt in to constant particle outlines"));
        config.addDefault("particles.allow_entry_particle_blinking", true); config.setInlineComments("particles.allow_entry_particle_blinking", List.of("If set to true, players will be able to toggle quarter outlines to blink when entered"));
        config.addDefault("particles.entry_particle_blinking_on_by_default", false); config.setInlineComments("particles.entry_particle_blinking_on_by_default", List.of("If set to true, quarters will blink their particles for one tick upon entry by a player, this can be a good alternative to constant particle outlines if it is causing lag"));

        config.options().copyDefaults(true);
    }
}
