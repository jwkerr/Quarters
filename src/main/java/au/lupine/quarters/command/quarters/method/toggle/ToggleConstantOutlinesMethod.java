package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleConstantOutlinesMethod extends CommandMethod {

    public ToggleConstantOutlinesMethod() {
        super("constantoutlines", "quarters.command.quarters.toggle.constantoutlines");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.toggle.ToggleConstantOutlinesMethod(source.getSender(), new String[0]).execute();
    }
}
