package au.lupine.quarters.command.quarters.method.set;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.ActionType;
import au.lupine.quarters.object.state.PermLevel;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class SetPermMethod extends CommandMethod {

    public SetPermMethod() {
        super("perm", "quarters.command.quarters.set.perm");
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> build() {
        RequiredArgumentBuilder<CommandSourceStack, String> allowedArgument = Commands.argument("allowed", StringArgumentType.word())
                .suggests((context, builder) -> suggestStrings(builder, "true", "false"))
                .executes(context -> run(context.getSource(), () -> execute(
                        context.getSource(),
                        context.getArgument("action", String.class),
                        context.getArgument("level", String.class),
                        context.getArgument("allowed", String.class)
                )));

        return super.build()
                .then(Commands.argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(ActionType.values()).map(ActionType::getLowerCase).toArray(String[]::new)))
                        .then(Commands.argument("level", StringArgumentType.word())
                                .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(PermLevel.values()).map(PermLevel::getLowerCase).toArray(String[]::new)))
                                .then(allowedArgument)));
    }

    @Override
    public void execute(@NotNull CommandSourceStack source) {
        throw new CommandMethodException("No action type provided");
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String action, @NotNull String permLevel, @NotNull String allowedValue) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        ActionType type;
        try {
            type = ActionType.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid action type provided");
        }

        PermLevel level;
        try {
            level = PermLevel.valueOf(permLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("Invalid perm level provided");
        }

        boolean allowed = Boolean.parseBoolean(allowedValue);

        quarter.getPermissions().setPermission(type, level, allowed);
        quarter.save();

        String lowerCaseLevel = level.name().toLowerCase();
        String lowerCaseType = type.getCommonName().toLowerCase();

        QuartersMessaging.sendSuccessMessage(player, "Successfully set " + lowerCaseLevel + " " + lowerCaseType + " permissions to " + allowed);
        QuartersMessaging.sendCommandFeedbackToTown(quarter.getTown(), player, "has set a quarter's " + lowerCaseLevel + " " + lowerCaseType + " permissions to " + allowed, player.getLocation());
    }
}
