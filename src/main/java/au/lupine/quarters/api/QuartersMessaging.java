package au.lupine.quarters.api;

import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.object.wrapper.Pair;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class QuartersMessaging {

    public static final Color PLUGIN_COLOUR = new Color(0x9655FF);
    public static final Component PLUGIN_WORDMARK_COMPONENT = Component.text("Quarters", TextColor.color(PLUGIN_COLOUR.getRGB()));
    public static final Component PREFIX_COMPONENT = PLUGIN_WORDMARK_COMPONENT.append(Component.text(" » ").color(NamedTextColor.DARK_GRAY));

    public static final Component MID_PIPE = Component.text("├ ", TextColor.color(PLUGIN_COLOUR.getRGB()));
    public static final Component BOTTOM_PIPE = Component.text("└ ", TextColor.color(PLUGIN_COLOUR.getRGB()));
    public static final Component OPEN_SQUARE_BRACKET = Component.text("[", NamedTextColor.DARK_GRAY);
    public static final Component CLOSED_SQUARE_BRACKET = Component.text("]", NamedTextColor.DARK_GRAY);

    public static void sendComponent(@NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(message);
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull Component message) {
        sendComponent(audience, PREFIX_COMPONENT.append(message));
    }

    public static void sendSuccessMessage(@NotNull Audience audience, @NotNull String message) {
        sendMessage(audience, Component.text(message, NamedTextColor.GREEN, TextDecoration.ITALIC));
    }

    public static void sendErrorMessage(@NotNull Audience audience, @NotNull String message) {
        sendMessage(audience, Component.text(message, NamedTextColor.RED, TextDecoration.ITALIC));
    }

    public static Component getListComponent(@NotNull Component header, @NotNull List<Pair<String, Component>> labelledEntries, @Nullable List<Pair<String, Component>> bracketEntries) {
        TextComponent.Builder builder = Component.text();
        builder.append(header);

        int labelledLength = labelledEntries.size();
        for (int i = 0; i < labelledLength; i++) {
            builder.appendNewline();

            if (i != labelledLength - 1) {
                builder.append(MID_PIPE);
            } else {
                builder.append(BOTTOM_PIPE);
            }

            Pair<String, Component> labelledEntry = labelledEntries.get(i);
            if (labelledEntry.getFirst() != null) builder.append(Component.text(labelledEntry.getFirst() + ": ", NamedTextColor.DARK_GRAY));
            builder.append(labelledEntry.getSecond());
        }

        if (bracketEntries == null) return builder.build();
        builder.appendNewline();

        for (Pair<String, Component> bracketEntry : bracketEntries) {
            TextComponent.Builder bracketBuilder = Component.text();

            bracketBuilder.append(OPEN_SQUARE_BRACKET)
                    .append(Component.text(bracketEntry.getFirst(), TextColor.color(PLUGIN_COLOUR.getRGB())))
                    .append(CLOSED_SQUARE_BRACKET).hoverEvent(bracketEntry.getSecond());

            builder.append(bracketBuilder.build());
            builder.appendSpace();
        }

        return builder.build();
    }

    public static void sendCommandFeedbackToTown(@NotNull Town town, @NotNull Player executingPlayer, @NotNull String message, @Nullable Location location) {
        TextComponent.Builder builder = Component.text();
        builder.append(ConfigManager.getFormattedName(executingPlayer.getUniqueId(), null));
        builder.appendSpace();
        builder.append(Component.text(message, NamedTextColor.GRAY));

        if (location != null) {
            builder.appendSpace();
            builder.append(Component.text("(", NamedTextColor.GRAY));
            builder.append(getLocationComponent(location));
            builder.append(Component.text(")", NamedTextColor.GRAY));
        }

        Component component = builder.build();

        for (Resident resident : town.getResidents()) {
            if (!resident.isOnline()) continue;

            Player player = resident.getPlayer();
            if (player == null) continue;

            boolean hasCommandFeedbackPerm = player.hasPermission("quarters.landlord.receive_command_feedback_from_town_members");
            boolean hasMayorPerm = resident.isMayor() && ConfigManager.doMayorsBypassCertainElevatedPerms();

            if (!hasCommandFeedbackPerm && !hasMayorPerm) continue;

            if (!player.equals(executingPlayer)) sendComponent(player, component);
        }
    }

    public static Component getLocationComponent(@NotNull Location location) {
        Component slash = Component.text("/", NamedTextColor.GRAY);

        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("X=" + location.getBlockX(), NamedTextColor.RED));
        builder.append(slash);
        builder.append(Component.text("Y=" + location.getBlockY(), NamedTextColor.GREEN));
        builder.append(slash);
        builder.append(Component.text("Z=" + location.getBlockZ(), NamedTextColor.BLUE));

        return builder.build();
    }
}
