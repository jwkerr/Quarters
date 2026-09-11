package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.object.base.CommandMethod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class AdminSetOwnerMethod extends CommandMethod {

    public AdminSetOwnerMethod() {
        super("owner", "quarters.command.quartersadmin.set.owner");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).toArray(String[]::new)))
                        .executes(context -> run(context.getSource(), () -> new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetOwnerMethod(context.getSource().getSender(), new String[]{context.getArgument("player", String.class)}).execute())));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        new au.lupine.quarters.command.quartersadmin.legacy_method.set.AdminSetOwnerMethod(source.getSender(), new String[0]).execute();
    }
}
