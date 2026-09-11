package au.lupine.quarters.command.quarters.method;

import au.lupine.quarters.command.quarters.method.edit.EditAddSelectionMethod;
import au.lupine.quarters.command.quarters.method.edit.EditRemoveMethod;
import au.lupine.quarters.object.base.CommandArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

public final class EditArgument extends CommandArgument {

    public EditArgument() {
        super("edit", "quarters.command.quarters.edit");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new EditAddSelectionMethod().build())
                .then(new EditRemoveMethod().build());
    }

}
