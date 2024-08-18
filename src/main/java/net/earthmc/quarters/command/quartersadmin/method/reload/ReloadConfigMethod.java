package net.earthmc.quarters.command.quartersadmin.method.reload;

import net.earthmc.quarters.api.QuartersMessaging;
import net.earthmc.quarters.api.manager.ConfigManager;
import net.earthmc.quarters.object.base.CommandMethod;
import org.bukkit.command.CommandSender;

public class ReloadConfigMethod extends CommandMethod {

    public ReloadConfigMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.reload.config");
    }

    @Override
    public void execute() {
        ConfigManager.getInstance().reload();

        QuartersMessaging.sendSuccessMessage(sender, "Successfully reloaded Quarters' config :3");
    }
}
