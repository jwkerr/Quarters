package au.lupine.quarters.hook;

import au.lupine.quarters.Quarters;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class QuartersPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "Quarters";
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
        return string;
    }
}
