package net.earthmc.quarters.command.quartersadmin.method;

import net.earthmc.quarters.command.quartersadmin.method.toggle.AdminToggleEmbassyMethod;
import net.earthmc.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class AdminToggleArgument extends CommandArgument {

    public AdminToggleArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.toggle");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "embassy" -> new AdminToggleEmbassyMethod(sender, args).execute();
        }
    }
}
