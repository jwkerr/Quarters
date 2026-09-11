package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.state.QuarterType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class AdminSetTypeMethod extends CommandMethod {

    public AdminSetTypeMethod() {
        super("type", "quarters.command.quartersadmin.set.type");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(QuarterType.values()).map(QuarterType::getLowerCase).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetTypeMethod(context.getSource().getSender(), new String[]{context.getArgument("type", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetTypeMethod(source.getSender(), new String[0]).execute();
    }
}
