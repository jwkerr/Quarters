package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.config.Config;
import net.earthmc.quarters.listener.*;
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

        Config.init(getConfig());
        saveConfig();

        WAND = Material.valueOf(getConfig().getString("wand_material"));

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
        getServer().getPluginManager().registerEvents(new DeletePlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemHeldListener(), this);

        if (getServer().getPluginManager().isPluginEnabled("quickshops"))
            getServer().getPluginManager().registerEvents(new ShopCreateListener(), this);

        getServer().getPluginManager().registerEvents(new TownUnclaimListener(), this);
        getServer().getPluginManager().registerEvents(new TownyActionListener(), this);
    }

    public void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new ClaimCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new DeleteCommand());
        manager.registerCommand(new EvictCommand());
        manager.registerCommand(new HereCommand());
        manager.registerCommand(new InfoCommand());
        manager.registerCommand(new PosCommand());
        manager.registerCommand(new SelectionCommand());
        manager.registerCommand(new SellCommand());
        manager.registerCommand(new TrustCommand());
        manager.registerCommand(new TypeCommand());
    }
}
