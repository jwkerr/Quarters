package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.config.Config;
import net.earthmc.quarters.listener.PlayerInteractListener;
import net.earthmc.quarters.listener.PlayerItemHeldListener;
import net.earthmc.quarters.listener.TownUnclaimListener;
import net.earthmc.quarters.listener.TownyActionEventListener;
import net.earthmc.quarters.object.QuarterListDFDeserializer;
import net.earthmc.quarters.object.QuarterListDataField;
import net.earthmc.quarters.task.OutlineParticleTask;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class Quarters extends JavaPlugin {
    public static Quarters INSTANCE;
    public static Material WAND;

    @Override
    public void onEnable() {
        INSTANCE = this;
        WAND = Material.valueOf(getConfig().getString("wand_material"));

        Config.init(getConfig());
        saveConfig();

        MetadataLoader.getInstance().registerDeserializer(QuarterListDataField.typeID(), new QuarterListDFDeserializer());

        initListeners();
        initCommands();

        OutlineParticleTask task = new OutlineParticleTask();
        task.runTaskTimer(this, 0, 10);

        getLogger().info("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        getLogger().info("Quarters disabled :v");
    }

    public void initListeners() {
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        getServer().getPluginManager().registerEvents(new TownUnclaimListener(), this);
        getServer().getPluginManager().registerEvents(new TownyActionEventListener(), this);
    }

    public void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new BuyCommand());
        manager.registerCommand(new ClearCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new DeleteCommand());
        manager.registerCommand(new HereCommand());
        manager.registerCommand(new InfoCommand());
        manager.registerCommand(new PosCommand());
        manager.registerCommand(new SellCommand());
        manager.registerCommand(new TrustCommand());
    }
}
