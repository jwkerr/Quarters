package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleEntryBlinkingMethod extends CommandMethod {

    public ToggleEntryBlinkingMethod() {
        super("entryblinking", "quarters.command.quarters.toggle.entryblinking");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.toggle.ToggleEntryBlinkingMethod(source.getSender(), new String[0]).execute();
    }
}
