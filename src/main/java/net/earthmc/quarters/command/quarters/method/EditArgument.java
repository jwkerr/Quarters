package net.earthmc.quarters.command.quarters.method;

import net.earthmc.quarters.command.quarters.method.edit.EditAddSelectionMethod;
import net.earthmc.quarters.command.quarters.method.edit.EditRemoveMethod;
import net.earthmc.quarters.object.base.CommandArgument;
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
