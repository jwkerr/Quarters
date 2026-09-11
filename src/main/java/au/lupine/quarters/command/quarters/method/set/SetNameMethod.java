package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class SetNameMethod extends CommandMethod {

    public SetNameMethod() {
        super("name", "quarters.command.quarters.set.name");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetNameMethod(context.getSource().getSender(), context.getArgument("name", String.class).split(" ")).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetNameMethod(source.getSender(), new String[0]).execute();
    }
}
