package net.earthmc.quarters.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuartersMessaging {
    private static Component getPrefixComponent() {
        return Component.text()
                .content("Quarters").color(TextColor.color(0x9655FF))
                .append(Component.text(" » ").color(NamedTextColor.DARK_GRAY))
                .build();
    }

    private static Component getHeaderComponent() {
        TextComponent lineComponent = Component.text()
                .content("-----")
                .color(NamedTextColor.DARK_GRAY)
                .build();

        TextComponent nameComponent = Component.text()
                .content(" Quarters ").color(TextColor.color(0x9655FF))
                .build();
        
        return Component.text()
                .append(lineComponent)
                .append(nameComponent)
                .append(lineComponent)
                .append(Component.text("\n"))
                .build();
    }

    /**
     * Sends a neutral, single-line, grey message to the specified player
     *
     * @param player Player that will receive the message
     * @param message Message that will be sent to the specified player
     */
    public static void sendInfoMessage(Player player, Component message) {
        player.sendMessage(getPrefixComponent()
                .append(message));
    }

    /**
     * Sends a neutral, single-line, grey message to the specified player
     *
     * @param player Player that will receive the message
     * @param message Message that will be sent to the specified player
     */
    public static void sendInfoMessage(Player player, String message) {
        player.sendMessage(getPrefixComponent()
                .append(Component.text(message).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)));
    }

    /**
     * Sends a header message with your attached text appended below it to the specified sender
     *
     * @param sender Player that will receive the message
     * @param message Message that will be sent to the specified player
     */
    public static void sendInfoWall(CommandSender sender, Component message) {
        sender.sendMessage(getHeaderComponent()
                .append(message));
    }

    /**
     * Sends a single-line, green success message to the specified player
     *
     * @param player Player that will receive the message
     * @param message Message that will be sent to the specified player
     */
    public static void sendSuccessMessage(Player player, String message) {
        player.sendMessage(getPrefixComponent()
                .append(Component.text(message).color(NamedTextColor.GREEN).decorate(TextDecoration.ITALIC)));
    }

    /**
     * Sends a single-line, red error message to the specified player
     *
     * @param player Player that will receive the message
     * @param message Message that will be sent to the specified player
     */
    public static void sendErrorMessage(Player player, String message) {
        player.sendMessage(getPrefixComponent()
                .append(Component.text(message).color(NamedTextColor.RED).decorate(TextDecoration.ITALIC)));
    }
}
