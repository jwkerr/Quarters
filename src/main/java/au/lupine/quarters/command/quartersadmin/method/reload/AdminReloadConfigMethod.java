package au.lupine.quarters.command.quartersadmin.method.reload;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminReloadConfigMethod extends CommandMethod {

    public AdminReloadConfigMethod() {
        super("config", "quarters.command.quartersadmin.reload.config");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.reload.AdminReloadConfigMethod(source.getSender(), new String[0]).execute();
    }
}
