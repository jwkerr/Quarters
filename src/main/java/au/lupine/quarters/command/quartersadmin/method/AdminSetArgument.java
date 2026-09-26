package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.set.*;
import au.lupine.quarters.object.base.CommandArgument;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminSetArgument extends CommandArgument {

    public AdminSetArgument() {
        super("set", "quarters.command.quartersadmin.set");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new AdminSetAnchorMethod().build())
                .then(new AdminSetColourMethod().build())
                .then(new AdminSetNameMethod().build())
                .then(new AdminSetOwnerMethod().build())
                .then(new AdminSetPermMethod().build())
                .then(new AdminSetTypeMethod().build());
    }
}
