package au.lupine.quarters.object.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class CommandMethodException extends RuntimeException {

    private final String message;
    private final Component component;

    public CommandMethodException(@NotNull String message) {
        this.message = message;
        this.component = Component.translatable(message, NamedTextColor.RED, TextDecoration.ITALIC);
    }

    /**
     * @return This command error without component formatting
     */
    @Override
    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull Component getComponent() {
        return component;
    }
}
