package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SelectionClearMethod extends CommandMethod {

    public SelectionClearMethod() {
        super("clear", "quarters.command.quarters.selection.clear");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.selection.SelectionClearMethod(source.getSender(), new String[0]).execute();
    }
}
