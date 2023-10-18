package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand_material", "FLINT"); config.setInlineComments("wand_material", Collections.singletonList("Material of the wand item in-game"));
        config.addDefault("current_selection_particle", "SCRAPE"); config.setInlineComments("current_selection_particle", Collections.singletonList("Particle outline of currently selected area"));
        config.addDefault("current_cuboids_particle", "WAX_OFF"); config.setInlineComments("current_cuboids_particle", Collections.singletonList("Particle outline of current cuboids added to selection"));
        config.addDefault("max_quarter_volume", 32768); config.setInlineComments("max_quarter_volume", Collections.singletonList("Maximum volume of all cuboids in a quarter combined"));

        config.options().copyDefaults(true);
    }
}
