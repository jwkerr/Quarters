package au.lupine.quarters.object.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class CommandMethodException extends RuntimeException {

    private final String message;
    private final Component component;
    private final ComponentLike[] arguments;

    public CommandMethodException(@NotNull String message, @NotNull ComponentLike... arguments) {
        this.message = message;
        this.arguments = arguments;
        this.component = Component.translatable(message, arguments).color(NamedTextColor.RED).decorate(TextDecoration.ITALIC);
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

    public @NotNull ComponentLike[] getArguments() {
        return arguments;
    }
}
