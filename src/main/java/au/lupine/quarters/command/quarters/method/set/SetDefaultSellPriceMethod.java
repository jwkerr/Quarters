package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public final class SetDefaultSellPriceMethod extends CommandMethod {

    public SetDefaultSellPriceMethod() {
        super("defaultsellprice", "quarters.command.quarters.set.defaultsellprice", true);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("price", DoubleArgumentType.doubleArg(0))
                        .suggests((context, builder) -> suggestStrings(builder, "0"))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.set.SetDefaultSellPriceMethod(context.getSource().getSender(), new String[]{Double.toString(context.getArgument("price", Double.class))}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.set.SetDefaultSellPriceMethod(source.getSender(), new String[0]).execute();
    }
}
