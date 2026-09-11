package au.lupine.quarters.command.quartersadmin.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminToggleEmbassyMethod extends CommandMethod {

    public AdminToggleEmbassyMethod() {
        super("embassy", "quarters.command.quartersadmin.toggle.embassy");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.toggle.AdminToggleEmbassyMethod(source.getSender(), new String[0]).execute();
    }
}
