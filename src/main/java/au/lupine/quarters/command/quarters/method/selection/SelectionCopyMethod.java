package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SelectionCopyMethod extends CommandMethod {

    public SelectionCopyMethod() {
        super("copy", "quarters.command.quarters.selection.copy");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.selection.SelectionCopyMethod(source.getSender(), new String[0]).execute();
    }
}
