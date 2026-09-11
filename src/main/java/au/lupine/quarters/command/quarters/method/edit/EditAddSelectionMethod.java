package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class EditAddSelectionMethod extends CommandMethod {

    public EditAddSelectionMethod() {
        super("addselection", "quarters.command.quarters.edit.addselection", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.edit.EditAddSelectionMethod(source.getSender(), new String[0]).execute();
    }
}
