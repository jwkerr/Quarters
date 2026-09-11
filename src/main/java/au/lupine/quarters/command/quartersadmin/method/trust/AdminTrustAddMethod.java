package au.lupine.quarters.command.quartersadmin.method.trust;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class AdminTrustAddMethod extends CommandMethod {

    public AdminTrustAddMethod() {
        super("add", "quarters.command.quartersadmin.trust.add");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.trust.AdminTrustAddMethod(context.getSource().getSender(), new String[]{context.getArgument("player", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.trust.AdminTrustAddMethod(source.getSender(), new String[0]).execute();
    }
}
