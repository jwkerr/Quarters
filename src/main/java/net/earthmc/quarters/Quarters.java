package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.config.Config;
import net.earthmc.quarters.listener.PlayerInteractListener;
import net.earthmc.quarters.object.QuarterListDFDeserializer;
import net.earthmc.quarters.object.QuarterListDataField;
import net.earthmc.quarters.task.SelectionParticleTask;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class Quarters extends JavaPlugin {
    public static JavaPlugin instance;
    public static Material wand;

    @Override
    public void onEnable() {
        Config.init(getConfig());
        saveConfig();

        instance = this;
        wand = Material.valueOf(getConfig().getString("wand"));

        MetadataLoader.getInstance()
                        .registerDeserializer(QuarterListDataField.typeID(), new QuarterListDFDeserializer());

        initListeners();
        initCommands();

        SelectionParticleTask task = new SelectionParticleTask();
        task.runTaskTimer(this, 0, 10);

        getLogger().info("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        getLogger().info("Quarters disabled :v");
    }

    public void initListeners() {
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    public void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new ClearCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new HereCommand());
        manager.registerCommand(new InfoCommand());
        manager.registerCommand(new Pos1Command());
        manager.registerCommand(new Pos2Command());
        manager.registerCommand(new TrustCommand());
    }
}
