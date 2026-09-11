package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SelectionRemoveMethod extends CommandMethod {

    public SelectionRemoveMethod() {
        super("remove", "quarters.command.quarters.selection.remove");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.selection.SelectionRemoveMethod(source.getSender(), new String[0]).execute();
    }
}
