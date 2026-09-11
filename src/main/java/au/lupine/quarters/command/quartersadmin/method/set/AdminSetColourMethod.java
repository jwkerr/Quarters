package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class AdminSetColourMethod extends CommandMethod {

    public AdminSetColourMethod() {
        super("colour", "quarters.command.quartersadmin.set.colour");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("hex", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, "#9655FF"))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetColourMethod(context.getSource().getSender(), new String[]{context.getArgument("hex", String.class)}).execute())))
                .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .suggests((context, builder) -> suggestStrings(builder, "150"))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                                .suggests((context, builder) -> suggestStrings(builder, "85"))
                                .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                        .suggests((context, builder) -> suggestStrings(builder, "255"))
                                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetColourMethod(context.getSource().getSender(), new String[]{
                                                Integer.toString(context.getArgument("r", Integer.class)),
                                                Integer.toString(context.getArgument("g", Integer.class)),
                                                Integer.toString(context.getArgument("b", Integer.class))
                                        }).execute())))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetColourMethod(source.getSender(), new String[0]).execute();
    }
}
