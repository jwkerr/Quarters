package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class SetPermMethod extends CommandMethod {

    public SetPermMethod() {
        super("perm", "quarters.command.quarters.set.perm");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(ActionType.values()).map(ActionType::getLowerCase).toArray(String[]::new)))
                        .then(Commands.argument("level", StringArgumentType.word())
                                .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(PermLevel.values()).map(PermLevel::getLowerCase).toArray(String[]::new)))
                                .then(Commands.argument("allowed", StringArgumentType.word())
                                        .suggests((context, builder) -> suggestStrings(builder, "true", "false"))
                                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetPermMethod(context.getSource().getSender(), new String[]{
                                                context.getArgument("action", String.class),
                                                context.getArgument("level", String.class),
                                                context.getArgument("allowed", String.class)
                                        }).execute())))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetPermMethod(source.getSender(), new String[0]).execute();
    }
}
