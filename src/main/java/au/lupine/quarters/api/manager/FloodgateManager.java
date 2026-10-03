package au.lupine.quarters.api.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FloodgateManager {

    private static FloodgateManager instance;
    private @Nullable FloodgateApi floodgateApi;

    private FloodgateManager() {}

    public static FloodgateManager getInstance() {
        if (instance == null) instance = new FloodgateManager();
        return instance;
    }

    public void setup(@NotNull Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        if (!pm.isPluginEnabled("floodgate")) return;

        try {
            floodgateApi = FloodgateApi.getInstance();
        } catch (Throwable ignored) {}
    }

    public boolean isBedrockPlayer(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) return false;
        return floodgateApi != null && floodgateApi.isFloodgatePlayer(player.getUniqueId());
    }
}
