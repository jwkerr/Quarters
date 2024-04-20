package net.earthmc.quarters;

import co.aikar.commands.PaperCommandManager;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.*;
import net.earthmc.quarters.command.admin.*;
import net.earthmc.quarters.config.Config;
import net.earthmc.quarters.listener.*;
import net.earthmc.quarters.manager.SponsorCosmeticsManager;
import net.earthmc.quarters.object.QuarterListDFDeserializer;
import net.earthmc.quarters.object.QuarterListDataField;
import net.earthmc.quarters.task.OutlineParticleTask;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
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

        SponsorCosmeticsManager.init();

        initCommands();
        initListeners();

        getLogger().info("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        getLogger().info("Quarters disabled :v");
    }

    private void initCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        // Standard commands
        manager.registerCommand(new ClaimCommand());
        manager.registerCommand(new ColourCommand());
        manager.registerCommand(new CreateCommand());
        manager.registerCommand(new DefaultSellPriceCommand());
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

    private void initListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new DeletePlayerListener(), this);
        pm.registerEvents(new PlayerDeniedBedUseListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);

        if (getConfig().getBoolean("particles.enabled"))
            pm.registerEvents(new PlayerItemHeldListener(), this);

        pm.registerEvents(new PlotPreClaimListener(), this);
        pm.registerEvents(new ResidentStatusScreenListener(), this);

        if (pm.isPluginEnabled("QuickShop") || pm.isPluginEnabled("QuickShop-Hikari"))
            pm.registerEvents(new ShopCreateListener(), this);

        pm.registerEvents(new TownRemoveResidentListener(), this);
        pm.registerEvents(new TownStatusScreenListener(), this);
        pm.registerEvents(new TownUnclaimListener(), this);
        pm.registerEvents(new TownyActionListener(), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
    }
}
