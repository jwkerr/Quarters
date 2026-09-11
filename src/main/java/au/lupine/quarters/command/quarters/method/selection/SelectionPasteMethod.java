package au.lupine.quarters.command.quarters.method.selection;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class SelectionPasteMethod extends CommandMethod {

    public SelectionPasteMethod() {
        super("paste", "quarters.command.quarters.selection.paste");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.selection.SelectionPasteMethod(source.getSender(), new String[0]).execute();
    }
}
