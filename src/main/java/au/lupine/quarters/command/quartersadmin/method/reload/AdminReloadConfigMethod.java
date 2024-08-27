package au.lupine.quarters.command.quartersadmin.method.reload;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.object.base.CommandMethod;
import org.bukkit.command.CommandSender;

public class AdminReloadConfigMethod extends CommandMethod {

    public AdminReloadConfigMethod(CommandSender sender, String[] args) {
        super(sender, args, "quarters.command.quartersadmin.reload.config");
    }

    @Override
    public void execute() {
        ConfigManager.getInstance().reload();

        QuartersMessaging.sendSuccessMessage(sender, "Successfully reloaded Quarters' config :3");
    }
}
