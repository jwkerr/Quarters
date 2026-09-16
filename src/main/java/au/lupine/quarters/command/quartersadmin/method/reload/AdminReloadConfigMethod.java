package au.lupine.quarters.command.quartersadmin.method.reload;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminReloadConfigMethod extends CommandMethod {

    public AdminReloadConfigMethod() {
        super("config", "quarters.command.quartersadmin.reload.config");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        ConfigManager.getInstance().reload();

        QuartersMessaging.sendSuccessMessage(source.getSender(), "quarters.command.quartersadmin.reload.config.feedback.success");
    }
}
