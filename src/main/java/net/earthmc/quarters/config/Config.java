package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", Collections.singletonList("Material of the wand item"));

        config.addDefault("quarters.max_quarter_volume", 0); config.setInlineComments("quarters.max_quarter_volume", Collections.singletonList("Maximum block volume of all cuboids in a quarter combined, set to 0 for no limit"));
        config.addDefault("quarters.max_quarters_per_town", 0); config.setInlineComments("quarters.max_quarters_per_town", Collections.singletonList("Maximum amount of quarters that can be in a single town, set to 0 for no limit"));
        config.addDefault("quarters.max_cuboid_volume", 0); config.setInlineComments("quarters.max_cuboid_volume", Collections.singletonList("Maximum block volume of individual cuboids, set to 0 for no limit"));
        config.addDefault("quarters.max_cuboids_per_quarter", 0); config.setInlineComments("quarters.max_cuboids_per_quarter", Collections.singletonList("Maximum amount of cuboids that can be in each quarter, set to 0 for no limit"));
        config.addDefault("quarters.default_colour.enabled", false); config.setInlineComments("quarters.default_colour.enabled", Collections.singletonList("Enable to make quarters a certain colour by default, configure colour below"));
        config.addDefault("quarters.default_colour.red", 63);
        config.addDefault("quarters.default_colour.green", 180);
        config.addDefault("quarters.default_colour.blue", 255);

        config.addDefault("particles.enabled", true); config.setInlineComments("particles.enabled", Collections.singletonList("Set to false to completely disable particle outlines around cuboids"));
        config.addDefault("particles.current_selection_particle", "SCRAPE"); config.setInlineComments("particles.current_selection_particle", Collections.singletonList("Particle outline of the currently selected area"));
        config.addDefault("particles.current_cuboids_particle", "WAX_OFF"); config.setInlineComments("particles.current_cuboids_particle", Collections.singletonList("Particle outline of current cuboids added to selection"));
        config.addDefault("particles.ticks_between_outline", 5); config.setInlineComments("particles.ticks_between_outline", Collections.singletonList("The number of ticks between when the particle outlines of quarters will appear"));
        config.addDefault("particles.max_distance_from_cuboid", 48); config.setInlineComments("particles.max_distance_from_cuboid", Collections.singletonList("The maximum distance a player can be from a cuboid before the outline particles stop being sent to their client"));
        config.addDefault("particles.allow_constant_outlines", true); config.setInlineComments("particles.allow_constant_outlines", Collections.singletonList("If set to true, players will be able to toggle quarter outlines to display constantly"));

        config.options().copyDefaults(true);
    }
}
