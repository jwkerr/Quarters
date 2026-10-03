package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.selection.SelectionAddMethod;
import au.lupine.quarters.command.quarters.method.selection.SelectionClearMethod;
import au.lupine.quarters.command.quarters.method.selection.SelectionCopyMethod;
import au.lupine.quarters.command.quarters.method.selection.SelectionPasteMethod;
import au.lupine.quarters.command.quarters.method.selection.SelectionRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

public final class SelectionArgument extends CommandArgument {

    public SelectionArgument() {
        super("selection", "quarters.command.quarters.selection");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new SelectionAddMethod().build())
                .then(new SelectionClearMethod().build())
                .then(new SelectionCopyMethod().build())
                .then(new SelectionPasteMethod().build())
                .then(new SelectionRemoveMethod().build());
    }

}
