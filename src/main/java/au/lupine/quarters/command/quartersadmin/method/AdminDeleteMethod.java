package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminDeleteMethod extends CommandMethod {

    public AdminDeleteMethod() {
        super("delete", "quarters.command.quartersadmin.delete");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.AdminDeleteMethod(source.getSender(), new String[0]).execute();
    }
}
