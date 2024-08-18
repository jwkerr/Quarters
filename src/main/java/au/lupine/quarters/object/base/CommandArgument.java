package au.lupine.quarters.object.base;

import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import org.bukkit.command.CommandSender;

/**
 * Represents an intermediary command argument
 */
public abstract class CommandArgument extends CommandMethod {

    public CommandArgument(CommandSender sender, String[] args, String permission) {
        super(sender, args, permission);
    }

    @Override
    public void execute() {
        if (args.length == 0) throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
        parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
    }

    protected abstract void parseMethod(CommandSender sender, String method, String[] args);
}
