package au.lupine.quarters.command.quartersadmin.method.trust;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminTrustClearMethod extends CommandMethod {

    public AdminTrustClearMethod() {
        super("clear", "quarters.command.quartersadmin.trust.clear");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.trust.AdminTrustClearMethod(source.getSender(), new String[0]).execute();
    }
}
