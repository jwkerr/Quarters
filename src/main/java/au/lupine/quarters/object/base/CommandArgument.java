package au.lupine.quarters.object.base;

import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandArgument extends CommandMethod {

    public CommandArgument(@NotNull String name, @Nullable String permission) {
        super(name, permission);
    }

    public CommandArgument(@NotNull String name, @Nullable String permission, boolean hasMayorPermBypass) {
        super(name, permission, hasMayorPermBypass);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }
}
