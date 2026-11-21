package au.lupine.quarters.object.exception;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A command failure containing several distinct errors that should be shown together.
 */
public final class MultiCommandMethodException extends CommandMethodException {

    private final List<CommandMethodException> errors;

    public MultiCommandMethodException(@NotNull List<CommandMethodException> errors) {
        super(errors.getFirst().getMessage(), errors.getFirst().getArguments());

        Map<String, CommandMethodException> distinctErrors = new LinkedHashMap<>();
        for (CommandMethodException error : errors) {
            distinctErrors.putIfAbsent(error.getMessage(), error);
        }

        this.errors = List.copyOf(distinctErrors.values());
    }

    public @NotNull List<CommandMethodException> getErrors() {
        return errors;
    }
}
