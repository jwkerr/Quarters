package au.lupine.quarters.object.base;

import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandArgument extends CommandMethod {

    public CommandArgument(@NotNull String name, @Nullable String permission) {
        super(name, permission);
    }

    public CommandArgument(@NotNull String name, @Nullable String permission, boolean hasMayorPermBypass) {
        super(name, permission, hasMayorPermBypass);
    }

    public CommandArgument(@NotNull CommandSender sender, String[] args, @Nullable String permission) {
        super(sender, args, permission);
    }

    public CommandArgument(@NotNull CommandSender sender, String[] args, @Nullable String permission, boolean hasMayorPermBypass) {
        super(sender, args, permission, hasMayorPermBypass);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    @Override
    public void execute() {
        if (args.length == 0) throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
        parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
    }

    protected void parseMethod(CommandSender sender, String method, String[] args) {}
}
