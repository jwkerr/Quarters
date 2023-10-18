package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.command.admin.*;
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

        OutlineParticleTask outlineTask = new OutlineParticleTask();
        outlineTask.runTaskTimer(this, 0, 5);

        getLogger().info("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        getLogger().info("Quarters disabled :v");
    }

    public void initListeners() {
        getServer().getPluginManager().registerEvents(new DeletePlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        getServer().getPluginManager().registerEvents(new PlotPreClaimListener(), this);

        if (getServer().getPluginManager().isPluginEnabled("QuickShop") || getServer().getPluginManager().isPluginEnabled("QuickShop-Hikari"))
            getServer().getPluginManager().registerEvents(new ShopCreateListener(), this);

        getServer().getPluginManager().registerEvents(new TownRemoveResidentListener(), this);
        getServer().getPluginManager().registerEvents(new TownStatusScreenListener(), this);
        getServer().getPluginManager().registerEvents(new TownUnclaimListener(), this);
        getServer().getPluginManager().registerEvents(new TownyActionListener(), this);
    }

    public void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        // Standard commands
        manager.registerCommand(new ClaimCommand());
        manager.registerCommand(new ColourCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new DeleteCommand());
        manager.registerCommand(new EvictCommand());
        manager.registerCommand(new HereCommand());
        manager.registerCommand(new InfoCommand());
        manager.registerCommand(new PosCommand());
        manager.registerCommand(new SelectionCommand());
        manager.registerCommand(new SellCommand());
        manager.registerCommand(new ToggleCommand());
        manager.registerCommand(new TrustCommand());
        manager.registerCommand(new TypeCommand());
        manager.registerCommand(new UnclaimCommand());

        // Admin commands
        manager.registerCommand(new AdminColourCommand());
        manager.registerCommand(new AdminDeleteCommand());
        manager.registerCommand(new AdminEvictCommand());
        manager.registerCommand(new AdminSellCommand());
        manager.registerCommand(new AdminSetOwnerCommand());
        manager.registerCommand(new AdminToggleCommand());
        manager.registerCommand(new AdminTrustCommand());
        manager.registerCommand(new AdminTypeCommand());
    }
}
