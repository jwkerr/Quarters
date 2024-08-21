package au.lupine.quarters.hook;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.manager.QuarterManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class QuartersPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "quarters";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", Quarters.getInstance().getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return Quarters.getInstance().getPluginMeta().getVersion();
    }

    // TODO: Implement placeholders
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String string) {
        if (player != null) return handlePlaceholder(player, string);

        return handlePlaceholderNullPlayer(string);
    }

    private String handlePlaceholder(@NotNull OfflinePlayer player, @NotNull String string) {
        return switch (string) {
            case "num_quarters" -> String.valueOf(QuarterManager.getInstance().getQuarters(player).size());
            case "num_cuboids" -> String.valueOf(QuarterManager.getInstance().getQuarters(player).stream().mapToInt(quarter -> quarter.getCuboids().size()).sum());
            default -> null;
        };
    }

    private String handlePlaceholderNullPlayer(@NotNull String string) {
        return switch (string) {
            case "num_quarters" -> String.valueOf(QuarterManager.getInstance().getAllQuarters().size());
            case "num_cuboids" -> String.valueOf(QuarterManager.getInstance().getAllQuarters().stream().mapToInt(quarter -> quarter.getCuboids().size()).sum());
            default -> null;
        };
    }
}
