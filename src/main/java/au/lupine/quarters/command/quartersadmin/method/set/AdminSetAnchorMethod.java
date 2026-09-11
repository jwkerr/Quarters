package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminSetAnchorMethod extends CommandMethod {

    public AdminSetAnchorMethod() {
        super("anchor", "quarters.command.quartersadmin.set.anchor");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetAnchorMethod(source.getSender(), new String[0]).execute();
    }
}
