package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand", "FLINT");
        config.addDefault("max_volume", 16384);
        config.addDefault("selection_particle", "SCRAPE");
        config.addDefault("created_particle", "WAX_OFF");

        config.options().copyDefaults(true);
    }
}
