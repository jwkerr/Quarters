package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class AdminMetaRemoveMethod extends CommandMethod {

    public AdminMetaRemoveMethod() {
        super("remove", "quarters.command.quartersadmin.meta.remove");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("key", StringArgumentType.word())
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.meta.AdminMetaRemoveMethod(context.getSource().getSender(), new String[]{context.getArgument("key", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.meta.AdminMetaRemoveMethod(source.getSender(), new String[0]).execute();
    }
}
