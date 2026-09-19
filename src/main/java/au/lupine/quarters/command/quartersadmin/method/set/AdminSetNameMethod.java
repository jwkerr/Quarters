package au.lupine.quarters.command.quartersadmin.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AdminSetNameMethod extends CommandMethod {

    public AdminSetNameMethod() {
        super("name", "quarters.command.quartersadmin.set.name");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(context -> run(context.getSource(), () -> execute(context.getSource(), context.getArgument("name", String.class)))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException(StringConstants.A_REQUIRED_ARGUMENT_WAS_NOT_PROVIDED);
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String name) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        int maxNameLength = 32;
        if (name.length() > maxNameLength) throw new CommandMethodException(
                "quarters.command.quarters.set.name.feedback.too_long",
                Argument.string("max", Integer.toString(maxNameLength))
        );

        quarter.setName(name);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.name.feedback.success", Argument.string("name", name));
    }
}
