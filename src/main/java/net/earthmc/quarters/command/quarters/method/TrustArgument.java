package net.earthmc.quarters.command.quarters.method;

import net.earthmc.quarters.command.quarters.method.trust.TrustAddMethod;
import net.earthmc.quarters.command.quarters.method.trust.TrustClearMethod;
import net.earthmc.quarters.command.quarters.method.trust.TrustRemoveMethod;
import net.earthmc.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class TrustArgument extends CommandArgument {

    public TrustArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quarters.trust");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "add" -> new TrustAddMethod(sender, args).execute();
            case "clear" -> new TrustClearMethod(sender, args).execute();
            case "remove" -> new TrustRemoveMethod(sender, args).execute();
        }
    }
}
