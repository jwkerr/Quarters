package au.lupine.quarters.command.quartersadmin.method;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class AdminPortMethod extends CommandMethod {

    public AdminPortMethod() {
        super("port", "quarters.command.quartersadmin.port");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("consoleOutput", BoolArgumentType.bool())
                        .suggests((context, builder) -> suggestStrings(builder, "true", "false"))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.AdminPortMethod(context.getSource().getSender(), new String[]{Boolean.toString(context.getArgument("consoleOutput", Boolean.class))}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.AdminPortMethod(source.getSender(), new String[0]).execute();
    }
}
