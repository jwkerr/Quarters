package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleEmbassyMethod extends CommandMethod {

    public ToggleEmbassyMethod() {
        super("embassy", "quarters.command.quarters.toggle.embassy", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.toggle.ToggleEmbassyMethod(source.getSender(), new String[0]).execute();
    }
}
