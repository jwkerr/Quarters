package au.lupine.quarters.command.quartersadmin.method.trust;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class AdminTrustRemoveMethod extends CommandMethod {

    public AdminTrustRemoveMethod() {
        super("remove", "quarters.command.quartersadmin.trust.remove");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.trust.AdminTrustRemoveMethod(context.getSource().getSender(), new String[]{context.getArgument("player", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.trust.AdminTrustRemoveMethod(source.getSender(), new String[0]).execute();
    }
}
