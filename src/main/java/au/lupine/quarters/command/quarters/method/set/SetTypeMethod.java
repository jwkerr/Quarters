package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.state.QuarterType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class SetTypeMethod extends CommandMethod {

    public SetTypeMethod() {
        super("type", "quarters.command.quarters.set.type", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(QuarterType.values()).map(QuarterType::getLowerCase).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetTypeMethod(context.getSource().getSender(), new String[]{context.getArgument("type", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetTypeMethod(source.getSender(), new String[0]).execute();
    }
}
