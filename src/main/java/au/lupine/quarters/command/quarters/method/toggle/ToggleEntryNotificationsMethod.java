package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class ToggleEntryNotificationsMethod extends CommandMethod {

    public ToggleEntryNotificationsMethod() {
        super("entrynotifications", "quarters.command.quarters.toggle.entrynotifications");
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.toggle.ToggleEntryNotificationsMethod(source.getSender(), new String[0]).execute();
    }
}
