package net.earthmc.quarters.api.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.earthmc.quarters.Quarters;
import net.earthmc.quarters.object.state.UserGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    private static ConfigManager instance;

    private static FileConfiguration config;
    private static final Map<UUID, UserGroup> USER_GROUPS = new ConcurrentHashMap<>();

    private ConfigManager() {}

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    public void setup() {
        reload();

        loadUserGroups();
    }

    public void reload() {
        Quarters plugin = Quarters.getInstance();
        config = plugin.getConfig();

        addValues();

        plugin.saveConfig();
        plugin.reloadConfig();
    }

    private void loadUserGroups() {
        InputStream inputStream = Quarters.getInstance().getResource("user_groups.json");
        if (inputStream == null) return;

        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            UserGroup userGroup = UserGroup.valueOf(entry.getKey());
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                USER_GROUPS.put(UUID.fromString(element.getAsString()), userGroup);
            }
        }
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static UserGroup getUserGroup(UUID uuid) {
        UserGroup userGroup = USER_GROUPS.get(uuid);
        return userGroup != null ? userGroup : UserGroup.DEFAULT;
    }

    public static Component getFormattedName(@Nullable UUID uuid, @NotNull Component def) {
        if (uuid == null) return def;

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        String name = player.getName();
        if (name == null) return def; // UUID didn't resolve to an actual player

        UserGroup userGroup = getUserGroup(uuid);

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text(name, TextColor.color(userGroup.getColour().getRGB())));

        String description = userGroup.getDescription();
        if (description != null) builder.hoverEvent(Component.text(description, NamedTextColor.GRAY));

        return builder.build();
    }

    public static Material getWandMaterial() {
        return Material.valueOf(config.getString("wand_material", "FLINT"));
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
        return config.getBoolean("quarters.default_colour.enabled", false);
    }

    public static Color getDefaultQuarterColour() {
        int r = config.getInt("quarters.default_quarter_colour.red", 63);
        int g = config.getInt("quarters.default_quarter_colour.green", 180);
        int b = config.getInt("quarters.default_quarter_colour.blue", 255);

        return new Color(r, g, b);
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

    public static boolean areConstantParticleOutlinesAllowed() {
        return config.getBoolean("particles.allow_constant_particle_outlines", true);
    }

    private void addValues() {
        config.options().setHeader(List.of("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", List.of("Material of the wand item"));

        config.addDefault("quarters.max_quarter_volume", -1); config.setInlineComments("quarters.max_quarter_volume", List.of("Maximum block volume of all cuboids in a quarter combined, set to -1 for no limit"));
        config.addDefault("quarters.max_quarters_per_town", -1); config.setInlineComments("quarters.max_quarters_per_town", List.of("Maximum amount of quarters that can be in a single town, set to -1 for no limit"));
        config.addDefault("quarters.max_cuboid_volume", -1); config.setInlineComments("quarters.max_cuboid_volume", List.of("Maximum block volume of individual cuboids, set to -1 for no limit"));
        config.addDefault("quarters.max_cuboids_per_quarter", -1); config.setInlineComments("quarters.max_cuboids_per_quarter", List.of("Maximum amount of cuboids that can be in each quarter, set to -1 for no limit"));
        config.addDefault("quarters.default_quarter_colour.enabled", false); config.setInlineComments("quarters.default_colour.enabled", List.of("Enable to make quarters a certain colour by default, configure colour below"));
        config.addDefault("quarters.default_quarter_colour.red", 63);
        config.addDefault("quarters.default_quarter_colour.green", 180);
        config.addDefault("quarters.default_quarter_colour.blue", 255);

        config.addDefault("particles.enabled", true); config.setInlineComments("particles.enabled", List.of("Set to false to completely disable particle outlines around cuboids"));
        config.addDefault("particles.current_selection_particle", "SCRAPE"); config.setInlineComments("particles.current_selection_particle", List.of("Particle outline of the currently selected area"));
        config.addDefault("particles.current_cuboids_particle", "WAX_OFF"); config.setInlineComments("particles.current_cuboids_particle", List.of("Particle outline of current cuboids added to selection"));
        config.addDefault("particles.ticks_between_particle_outlines", 5); config.setInlineComments("particles.ticks_between_particle_outlines", List.of("The number of ticks between when the particle outlines of quarters will appear"));
        config.addDefault("particles.max_distance_for_cuboid_particles", 48); config.setInlineComments("particles.max_distance_for_cuboid_particles", List.of("The maximum distance a player can be from a cuboid before the outline particles stop being sent to their client"));
        config.addDefault("particles.allow_constant_particle_outlines", true); config.setInlineComments("particles.allow_constant_particle_outlines", List.of("If set to true, players will be able to toggle quarter outlines to display constantly"));

        config.options().copyDefaults(true);
    }
}
