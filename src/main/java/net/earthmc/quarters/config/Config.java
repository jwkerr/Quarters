package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", Collections.singletonList("Material of the wand item in-game"));
        config.addDefault("current_selection_particle", "WAX_ON"); config.setInlineComments("current_selection_particle", Collections.singletonList("Particle outline of currently selected area"));
        config.addDefault("current_cuboids_particle", "SCRAPE"); config.setInlineComments("current_cuboids_particle", Collections.singletonList("Particle outline of current cuboids added to selection"));
        config.addDefault("created_particle", "WAX_OFF"); config.setInlineComments("created_particle", Collections.singletonList("Particle outline of pre-existing quarters"));

        config.options().copyDefaults(true);
    }
}
