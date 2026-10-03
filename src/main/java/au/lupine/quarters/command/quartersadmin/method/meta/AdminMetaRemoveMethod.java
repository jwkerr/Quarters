package au.lupine.quarters.command.quartersadmin.method.meta;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AdminMetaRemoveMethod extends CommandMethod {

    public AdminMetaRemoveMethod() {
        super("remove", "quarters.command.quartersadmin.meta.remove");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("key", StringArgumentType.word())
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("key", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.quartersadmin.meta.feedback.no_key");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String key) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.removeMetaData(key, true)) throw new CommandMethodException("quarters.command.quartersadmin.meta.remove.feedback.missing", Argument.string("key", key));

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quartersadmin.meta.remove.feedback.success", Argument.string("key", key));
    }
}
