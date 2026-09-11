package au.lupine.quarters.command.quarters.method.trust;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class TrustAddMethod extends CommandMethod {

    public TrustAddMethod() {
        super("add", "quarters.command.quarters.trust.add");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quarters.legacy_method.trust.TrustAddMethod(context.getSource().getSender(), new String[]{context.getArgument("player", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quarters.legacy_method.trust.TrustAddMethod(source.getSender(), new String[0]).execute();
    }
}
