package au.lupine.quarters.command.quarters.method.delete;

import au.lupine.quarters.object.base.CommandMethod;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class DeletePlotMethod extends CommandMethod {

    public DeletePlotMethod() {
        super("plot", "quarters.command.quarters.delete.plot", true);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.delete.DeletePlotMethod(source.getSender(), new String[0]).execute();
    }
}
