package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SelectionAddMethod extends CommandMethod {

    public SelectionAddMethod() {
        super("add", "quarters.command.quarters.selection.add");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.selection.SelectionAddMethod(source.getSender(), new String[0]).execute();
    }
}
