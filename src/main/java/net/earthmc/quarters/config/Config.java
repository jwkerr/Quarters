package net.earthmc.quarters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;

public class Config {
    public static void init(FileConfiguration config) {
        config.options().setHeader(Collections.singletonList("Quarters"));

        config.addDefault("wand", "FLINT");
        config.addDefault("max_volume", 16384);

        config.options().copyDefaults(true);
    }
}
