package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", Collections.singletonList("Material of the wand item in-game"));
        config.addDefault("max_quarter_volume", 16384); config.setInlineComments("max_quarter_volume", Collections.singletonList("Maximum volume of quarters"));
        config.addDefault("selection_particle", "SCRAPE"); config.setInlineComments("selection_particle", Collections.singletonList("Particle outline of currently selected area"));
        config.addDefault("created_particle", "WAX_OFF"); config.setInlineComments("created_particle", Collections.singletonList("Particle outline of pre-existing quarters"));
        config.addDefault("max_particle_volume", 16384); config.setInlineComments("max_outline_volume", Collections.singletonList("Maximum allowed cuboid volume to have an outline"));

        config.options().copyDefaults(true);
    }
}
