package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SetAnchorMethod extends CommandMethod {

    public SetAnchorMethod() {
        super("anchor", "quarters.command.quarters.set.anchor");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetAnchorMethod(source.getSender(), new String[0]).execute();
    }
}
