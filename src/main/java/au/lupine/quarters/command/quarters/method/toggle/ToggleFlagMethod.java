package au.lupine.quarters.command.quarters.method.toggle;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.object.base.CommandMethod;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.exception.CommandMethodException;
import au.lupine.quarters.object.state.FlagType;
import au.lupine.quarters.object.wrapper.StringConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ToggleFlagMethod extends CommandMethod {

    public ToggleFlagMethod() {
        super("flag", null);
    }

    public @NotNull RequiredArgumentBuilder<CommandSourceStack, String> buildArgument() {
        return Commands.argument("flag", StringArgumentType.word())
                .suggests((context, builder) -> suggestStrings(builder, Arrays.stream(FlagType.values())
                        .filter(flag -> Boolean.TRUE.equals(Quarters.getInstance().config().quarters.allowedFlags.get(flag)))
                        .map(FlagType::getLowerCase)
                        .toArray(String[]::new)))
                .executes(context -> run(context.getSource(), () -> execute(
                        context.getSource(),
                        context.getArgument("flag", String.class)
                )));
    }

    private void execute(@NotNull CommandSourceStack source, @NotNull String flagArgument) {
        Player player = getSenderAsPlayerOrThrow(source);
        Quarter quarter = getQuarterAtPlayerOrThrow(player);

        if (!quarter.hasBasicCommandPermissions(player)) throw new CommandMethodException(StringConstants.YOU_DO_NOT_HAVE_PERMISSION_TO_PERFORM_THIS_ACTION);

        FlagType flag;
        try {
            flag = FlagType.valueOf(flagArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandMethodException("quarters.command.quarters.toggle.flag.feedback.invalid_flag");
        }

        if (!quarter.isFlagAllowed(flag)) throw new CommandMethodException("quarters.command.quarters.toggle.flag.feedback.disabled");
        if (!source.getSender().hasPermission("quarters.command.quarters.toggle." + flag.getLowerCase()))
            throw new CommandMethodException("quarters.command.feedback.no_method_permission");

        boolean enabled = !quarter.hasFlag(flag);
        quarter.setFlag(flag, enabled);
        quarter.save();

        QuartersMessaging.sendSuccessMessage(
                player,
                enabled ? "quarters.command.quarters.toggle.flag.feedback.enabled" : "quarters.command.quarters.toggle.flag.feedback.disabled_success",
                Argument.string("flag", flag.getCommonName())
        );
        QuartersMessaging.sendCommandFeedbackToTown(
                quarter.getTown(),
                player,
                enabled ? "quarters.command.quarters.toggle.flag.feedback.town.enabled" : "quarters.command.quarters.toggle.flag.feedback.town.disabled",
                player.getLocation(),
                Argument.string("flag", flag.getCommonName())
        );
    }
}
