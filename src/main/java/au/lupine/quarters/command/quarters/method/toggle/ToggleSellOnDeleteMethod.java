package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleSellOnDeleteMethod extends CommandMethod {

    public ToggleSellOnDeleteMethod() {
        super("sellondelete", "quarters.command.quarters.toggle.sellondelete", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.toggle.ToggleSellOnDeleteMethod(source.getSender(), new String[0]).execute();
    }
}
