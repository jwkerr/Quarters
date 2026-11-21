package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class SetPermMethod extends CommandMethod {

    public SetPermMethod() {
        super("perm", "quarters.command.quarters.set.perm");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        return super.build()
                .then(allowedLiteral(true, null, null))
                .then(allowedLiteral(false, null, null))
                .then(Commands.argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(ActionType.values()).map(ActionType::getLowerCase).toArray(String[]::new)))
                        .then(allowedLiteral(true, "action", null))
                        .then(allowedLiteral(false, "action", null))
                        .then(Commands.argument("level", StringArgumentType.word())
                                .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(PermLevel.values()).map(PermLevel::getLowerCase).toArray(String[]::new)))
                                .then(allowedLiteral(true, "action", "level"))
                                .then(allowedLiteral(false, "action", "level"))));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("quarters.command.quarters.set.perm.feedback.no_action");
    }

    private @NotNull LiteralArgumentBuilder<CommandSourceStack> allowedLiteral(boolean allowed, @Nullable String actionArgument, @Nullable String levelArgument) {
        return Commands.literal(Boolean.toString(allowed))
                .executes(context -> run(context.getSource(), () -> execute(
                        context.getSource(),
                        actionArgument == null ? null : context.getArgument(actionArgument, String.class),
                        levelArgument == null ? null : context.getArgument(levelArgument, String.class),
                        allowed
                )));
    }

    private void execute(@NotNull CommandSourceStack source, @Nullable String action, @Nullable String permLevel, boolean allowed) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException("quarters.command.feedback.no_action_permission");

        ActionType type = null;
        if (action != null) {
            try {
                type = ActionType.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommandMethodException("quarters.command.quarters.set.perm.feedback.invalid_action");
            }
        }

        PermLevel level = null;
        if (permLevel != null) {
            try {
                level = PermLevel.valueOf(permLevel.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommandMethodException("quarters.command.quarters.set.perm.feedback.invalid_level");
            }
        }

        if (type == null) {
            quarter.getPermissions().setPermissions(allowed);
        } else if (level == null) {
            quarter.getPermissions().setPermissions(type, allowed);
        } else {
            quarter.getPermissions().setPermission(type, level, allowed);
        }
        quarter.save();

        if (type == null) {
            sendFeedback(player, quarter, "quarters.command.quarters.set.perm.feedback.all", "quarters.command.quarters.set.perm.feedback.town.all", allowed);
        } else if (level == null) {
            sendFeedback(player, quarter, "quarters.command.quarters.set.perm.feedback.action", "quarters.command.quarters.set.perm.feedback.town.action", allowed, Argument.string("action", type.getCommonName()));
        } else {
            sendFeedback(player, quarter, "quarters.command.quarters.set.perm.feedback.success", "quarters.command.quarters.set.perm.feedback.town", allowed,
                    Argument.string("level", level.getCommonName()), Argument.string("action", type.getCommonName()));
        }
    }

    private void sendFeedback(@NotNull Player player, @NotNull Quarter quarter, @NotNull String message, @NotNull String townMessage, boolean allowed, ComponentLike... arguments) {
        ComponentLike[] allArguments = Arrays.copyOf(arguments, arguments.length + 1);
        allArguments[arguments.length] = Argument.string("allowed", Boolean.toString(allowed));

        QuartersMessaging.sendSuccessMessage(player, message, allArguments);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, townMessage, player.getLocation(), allArguments);
    }
}
