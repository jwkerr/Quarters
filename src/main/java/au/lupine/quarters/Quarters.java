package au.lupine.quarters;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.command.quarters.QuartersCommand;
import au.lupine.quarters.command.quartersadmin.QuartersAdminCommand;
import au.lupine.quarters.hook.QuartersPlaceholderExpansion;
import au.lupine.quarters.listener.*;
import au.lupine.quarters.object.metadata.QuarterListDataField;
import au.lupine.quarters.object.metadata.QuarterListDataFieldDeserialiser;
import au.lupine.quarters.object.wrapper.Pair;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
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
        registerCommands(
                Pair.of("quarters", new QuartersCommand()),
                Pair.of("quartersadmin", new QuartersAdminCommand())
        );

        registerHooks();

        registerListeners(
                new QuarterEntryNotificationListener(),
                new QuarterIntegrityListener(),
                new QuarterParticleListener(),
                new QuartersWandListener(),
                new StatusScreenListener(),
                new TownyActionListener()
        );

        logInfo("Quarters enabled :3");
    }

    @Override
    public void onDisable() {
        logInfo("Quarters disabled :v");
    }

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();

        ConfigManager.getInstance().setup();

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

    private void registerHooks() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.getPlugin("PlaceholderAPI") != null) new QuartersPlaceholderExpansion().register();
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
