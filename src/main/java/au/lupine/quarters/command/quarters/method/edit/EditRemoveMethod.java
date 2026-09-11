package au.lupine.quarters.command.quarters.method.edit;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class EditRemoveMethod extends CommandMethod {

    public EditRemoveMethod() {
        super("remove", "quarters.command.quarters.edit.remove", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.edit.EditRemoveMethod(source.getSender(), new String[0]).execute();
    }
}
