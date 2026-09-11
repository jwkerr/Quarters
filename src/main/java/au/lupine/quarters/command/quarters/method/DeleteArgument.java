package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.delete.DeleteAllMethod;
import au.lupine.quarters.command.quarters.method.delete.DeletePlotMethod;
import au.lupine.quarters.object.base.CommandArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

public final class DeleteArgument extends CommandArgument {

    public DeleteArgument() {
        super("delete", "quarters.command.quarters.delete", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new DeleteAllMethod().build())
                .then(new DeletePlotMethod().build());
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.DeleteArgument(source.getSender(), new String[0]).execute();
    }
}
