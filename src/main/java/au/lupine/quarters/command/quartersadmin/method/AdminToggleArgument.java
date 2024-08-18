package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.toggle.AdminToggleEmbassyMethod;
import au.lupine.quarters.object.base.CommandArgument;
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
