package net.earthmc.quarters.command.quarters.method;

import net.earthmc.quarters.command.quarters.method.set.SetColourMethod;
import net.earthmc.quarters.command.quarters.method.set.SetDefaultSellPriceMethod;
import net.earthmc.quarters.command.quarters.method.set.SetTypeMethod;
import net.earthmc.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class SetArgument extends CommandArgument {

    public SetArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.set");
    }

    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "colour" -> new SetColourMethod(sender, args).execute();
            case "defaultsellprice" -> new SetDefaultSellPriceMethod(sender, args).execute();
            case "type" -> new SetTypeMethod(sender, args).execute();
        }
    }
}
