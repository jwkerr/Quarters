package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustAddMethod;
import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustClearMethod;
import au.lupine.quarters.command.quartersadmin.method.trust.AdminTrustRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class AdminTrustArgument extends CommandArgument {

    public AdminTrustArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.trust");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "add" -> new AdminTrustAddMethod(sender, args).execute();
            case "clear" -> new AdminTrustClearMethod(sender, args).execute();
            case "remove" -> new AdminTrustRemoveMethod(sender, args).execute();
        }
    }
}
