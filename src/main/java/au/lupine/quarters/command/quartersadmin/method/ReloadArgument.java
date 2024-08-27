package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.reload.AdminReloadConfigMethod;
import au.lupine.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class ReloadArgument extends CommandArgument {

    public ReloadArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.reload");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "config" -> new AdminReloadConfigMethod(sender, args).execute();
        }
    }
}
