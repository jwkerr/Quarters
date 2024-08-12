package net.earthmc.quarters;

import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import net.earthmc.quarters.command.quarters.QuartersCommand;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.listener.*;
import net.earthmc.quarters.object.wrapper.Pair;
import net.earthmc.quarters.object.metadata.QuarterListDataField;
import net.earthmc.quarters.object.metadata.QuarterListDataFieldDeserialiser;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Quarters extends JavaPlugin {

    private static Quarters instance;

    private static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        ConfigManager.getInstance().setup();

        registerCommands(
                Pair.of("quarters", new QuartersCommand())
        );

        registerListeners(
            new DeletePlayerListener(),
            new PlayerDeniedBedUseListener(),
            new PlayerInteractListener(),
            new PlayerItemHeldListener(),
            new PlotPreClaimListener(),
            new ResidentStatusScreenListener(),
            new TownRemoveResidentListener(),
            new TownStatusScreenListener(),
            new TownUnclaimListener(),
            new TownyActionListener(),
            new PlayerJoinListener()
        );

        logInfo("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        logInfo("Quarters disabled :v");
    }

    @Override
    public void onLoad() {
        MetadataLoader.getInstance().registerDeserializer(QuarterListDataField.typeID(), new QuarterListDataFieldDeserialiser());
    }

    @SafeVarargs
    private void registerCommands(Pair<String, CommandExecutor>... commandPair) {
        for (Pair<String, CommandExecutor> pair : commandPair) {
            String name = pair.getFirst();

            PluginCommand command = getCommand(name);
            if (command == null) {
                logSevere("Command " + name + " was null, failed to set a command executor");
                continue;
            }

            command.setExecutor(pair.getSecond());
        }
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pm = getServer().getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    public static Quarters getInstance() {
        return instance;
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }

    public static void logWarning(String msg) {
        logger.warning(msg);
    }

    public static void logSevere(String msg) {
        logger.severe(msg);
    }
}
