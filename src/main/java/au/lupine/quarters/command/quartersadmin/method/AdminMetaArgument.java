package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.command.quartersadmin.method.meta.AdminMetaRemoveMethod;
import au.lupine.quarters.command.quartersadmin.method.meta.AdminMetaSetMethod;
import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public final class AdminMetaArgument extends CommandMethod {

    public AdminMetaArgument() {
        super("meta", "quarters.command.quartersadmin.meta");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(new AdminMetaRemoveMethod().build())
                .then(new AdminMetaSetMethod().build());
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.AdminMetaArgument(source.getSender(), new String[0]).execute();
    }
}
