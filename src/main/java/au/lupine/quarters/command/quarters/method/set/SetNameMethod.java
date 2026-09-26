package au.lupine.quarters.command.quarters.method.set;

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

public final class SetNameMethod extends CommandMethod {

    public SetNameMethod() {
        super("name", "quarters.command.quarters.set.name");
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

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        int maxNameLength = 32;
        if (name.length() > maxNameLength) throw new CommandMethodException(
                "quarters.command.quarters.set.name.feedback.too_long",
                Argument.string("max", Integer.toString(maxNameLength))
        );

        quarter.setName(name);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(player, "quarters.command.quarters.set.name.feedback.success", Argument.string("name", name));
        QuartersMessaging.sendCommandFeedbackToTown(
                quarter.getTown(),
                player,
                "quarters.command.quarters.set.name.feedback.town",
                quarter.getFirstCornerOfFirstCuboid(),
                Argument.string("name", name)
        );
    }
}
