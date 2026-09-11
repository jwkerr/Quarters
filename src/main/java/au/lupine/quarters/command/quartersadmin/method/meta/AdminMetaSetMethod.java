package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class AdminMetaSetMethod extends CommandMethod {

    public AdminMetaSetMethod() {
        super("set", "quarters.command.quartersadmin.meta.set");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("key", StringArgumentType.word())
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.meta.AdminMetaSetMethod(context.getSource().getSender(), new String[]{
                                        context.getArgument("key", String.class),
                                        context.getArgument("value", String.class)
                                }).execute()))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.meta.AdminMetaSetMethod(source.getSender(), new String[0]).execute();
    }
}
