package net.earthmc.quarters.object.base;

import net.earthmc.quarters.object.exception.CommandMethodException;
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
        if (args.length == 0) throw new CommandMethodException("Missing argument"); // TODO: improve this error
        parseMethod(sender, args[0].toLowerCase(), CommandMethod.removeFirstArgument(args));
    }

    protected abstract void parseMethod(CommandSender sender, String method, String[] args);
}
