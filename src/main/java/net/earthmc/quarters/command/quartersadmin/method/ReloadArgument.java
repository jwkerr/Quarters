package net.earthmc.quarters.command.quartersadmin.method;

import net.earthmc.quarters.command.quartersadmin.method.reload.ReloadConfigMethod;
import net.earthmc.quarters.object.base.CommandArgument;
import org.bukkit.command.CommandSender;

public class ReloadArgument extends CommandArgument {

    public ReloadArgument(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.reload");
    }

    @Override
    protected void parseMethod(CommandSender sender, String method, String[] args) {
        switch (method) {
            case "config" -> new ReloadConfigMethod(sender, args).execute();
        }
    }
}
