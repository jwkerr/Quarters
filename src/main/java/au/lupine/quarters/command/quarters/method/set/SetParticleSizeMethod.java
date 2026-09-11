package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class SetParticleSizeMethod extends CommandMethod {

    public SetParticleSizeMethod() {
        super("particlesize", "quarters.command.quarters.set.particlesize");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("size", FloatArgumentType.floatArg(0.0F, 4.0F))
                        .suggests((context, builder) -> suggestStrings(builder, "1.0"))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetParticleSizeMethod(context.getSource().getSender(), new String[]{Float.toString(context.getArgument("size", Float.class))}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetParticleSizeMethod(source.getSender(), new String[0]).execute();
    }
}
