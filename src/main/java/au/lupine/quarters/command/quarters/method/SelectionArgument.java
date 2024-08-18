package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.selection.*;
import au.lupine.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class SelectionArgument extends CommandArgument {

    public SelectionArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.selection");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "add" -> new SelectionAddMethod(sender, args).execute();
            case "clear" -> new SelectionClearMethod(sender, args).execute();
            case "copy" -> new SelectionCopyMethod(sender, args).execute();
            case "paste" -> new SelectionPasteMethod(sender, args).execute();
            case "remove" -> new SelectionRemoveMethod(sender, args).execute();
        }
    }
}
