package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.edit.EditAddSelectionMethod;
import au.lupine.quarters.command.quarters.method.edit.EditRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class EditArgument extends CommandArgument {

    public EditArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.edit");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "addselection" -> new EditAddSelectionMethod(sender, args).execute();
            case "remove" -> new EditRemoveMethod(sender, args).execute();
        }
    }
}
