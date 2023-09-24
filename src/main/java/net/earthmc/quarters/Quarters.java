package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.config.Config;
import net.earthmc.quarters.listener.PlayerInteractListener;
import net.earthmc.quarters.task.SelectionParticleTask;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class Quarters extends JavaPlugin {
    public static JavaPlugin plugin;
    public final FileConfiguration config = getConfig();
    public static Material wand;

    @Override
    public void onEnable() {
        Config.init(config);
        saveConfig();

        plugin = this;
        wand = Material.valueOf(config.getString("wand"));

        initListeners();
        initCommands();

        SelectionParticleTask task = new SelectionParticleTask();
        task.runTaskTimer(this, 0, 10);

        getLogger().info("Quarters enabled :3");
    }

    public void initListeners() {
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    public void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new ClearCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new InfoCommand());
        manager.registerCommand(new Pos1Command());
        manager.registerCommand(new Pos2Command());
    }

    @Override
    public void onDisable() {
        getLogger().info("Quarters disabled :v");
    }
}
