package net.earthmc.quarters.command.quartersadmin.method;

import net.earthmc.quarters.command.quartersadmin.method.set.*;
import net.earthmc.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class AdminSetArgument extends CommandArgument {

    public AdminSetArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.set");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "anchor" -> new AdminSetAnchorMethod(sender, args).execute();
            case "colour" -> new AdminSetColourMethod(sender, args).execute();
            case "name" -> new AdminSetNameMethod(sender, args).execute();
            case "owner" -> new AdminSetOwnerMethod(sender, args).execute();
            case "perm" -> new AdminSetPermMethod(sender, args).execute();
            case "type" -> new AdminSetTypeMethod(sender, args).execute();
        }
    }
}
