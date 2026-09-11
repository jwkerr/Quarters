package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminEvictMethod extends CommandMethod {

    public AdminEvictMethod() {
        super("evict", "quarters.command.quartersadmin.evict");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.AdminEvictMethod(source.getSender(), new String[0]).execute();
    }
}
