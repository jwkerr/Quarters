package au.lupine.quarters.object.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CommandMethodException extends RuntimeException {

    private final String message;
    private final Component component;

    public CommandMethodException(String message) {
        this.message = message;
        this.component = Component.text(message, NamedTextColor.RED, TextDecoration.ITALIC);
    }

    /**
     * @return This command error without component formatting
     */
    @Override
    public String getMessage() {
        return message;
    }

    public Component getComponent() {
        return component;
    }
}
