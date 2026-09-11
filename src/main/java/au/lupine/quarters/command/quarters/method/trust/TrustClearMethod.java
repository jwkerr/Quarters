package au.lupine.quarters.command.quarters.method.trust;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class TrustClearMethod extends CommandMethod {

    public TrustClearMethod() {
        super("clear", "quarters.command.quarters.trust.clear");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.trust.TrustClearMethod(source.getSender(), new String[0]).execute();
    }
}
