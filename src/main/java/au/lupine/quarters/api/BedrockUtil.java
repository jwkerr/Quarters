package au.lupine.quarters.api;

import au.lupine.quarters.api.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class BedrockUtil {

    private static Boolean floodgatePresent = null;

    public static boolean isFloodgatePresent() {
        if (floodgatePresent == null) {
            floodgatePresent = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        }
        return floodgatePresent;
    }

    public static boolean isBedrockPlayer(Player player) {
        if (isFloodgatePresent()) {
            try {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return true;
            } catch (Exception ignored) {}
        }

        if (ConfigManager.isBedrockPrefixFallbackEnabled())
            return player.getName().startsWith(ConfigManager.getBedrockUsernamePrefix());

        return false;
    }
}
